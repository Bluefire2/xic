package kc875.asm.visit;

import com.google.common.collect.Sets;
import kc875.asm.*;
import kc875.asm.dfa.ASMGraph;
import kc875.asm.dfa.ASMLiveVariableDFA;
import kc875.asm.dfa.InterferenceGraph;
import kc875.asm.visit.RegAllocationOptimVisitor.SpillMode;
import kc875.cfg.Graph;
import polyglot.util.InternalCompilerError;
import polyglot.util.Pair;

import java.util.*;

public class RegAllocationColoringVisitor {

    private List<String> usableRegisters;

    //every node is in exactly one of these sets
    private HashSet<Graph<ASMExprRT>.Node> precolored; // machine registers, pre-assigned
    private HashSet<Graph<ASMExprRT>.Node> initial; // temps, not processed yet
    private HashSet<Graph<ASMExprRT>.Node> simplifyWorklist; //low degree, non move related nodes
    private HashSet<Graph<ASMExprRT>.Node> freezeWorklist;//low degree, move related nodes
    private HashSet<Graph<ASMExprRT>.Node> spillWorklist;//high degree nodes
    private HashSet<Graph<ASMExprRT>.Node> spilledNodes;//marked for spilling this round, initially empty
    private HashSet<Graph<ASMExprRT>.Node> coalescedNodes;//registers which are coalesced
    //u <- v coalesced then v is added to this set, u is moved to work list
    private HashSet<Graph<ASMExprRT>.Node> coloredNodes; //successfully colored
    private Stack<Graph<ASMExprRT>.Node> selectStack; //stacks w/ temps removed from graph

    //every move is in exactly one of these sets
    private HashSet<ASMInstr> coalescedMoves; //coalesced moves
    private HashSet<ASMInstr> constrainedMoves; //moves whose src/target interfere
    private HashSet<ASMInstr> frozenMoves; //moves no longer considered for coalescing
    private HashSet<ASMInstr> worklistMoves; //moves enabled for possible coalescing
    private HashSet<ASMInstr> activeMoves; //moves not ready for coalescing

    //interference graph abstraction
    //set of interference edges - reverse edges must always be in the set
    private HashSet<Pair<Graph<ASMExprRT>.Node, Graph<ASMExprRT>.Node>> adjSet;

    //list repr of the graph, node -> list of interfering nodes
    private HashMap<Graph<ASMExprRT>.Node, Set<Graph<ASMExprRT>.Node>> adjList;

    //Degree of each node
    private HashMap<Graph<ASMExprRT>.Node, Integer> degree;

    //map from node -> list of moves it is associated with
    private HashMap<Graph<ASMExprRT>.Node, HashSet<ASMInstr>> moveList;

    //alias - when MOV(u,v) coalesced, then v is in coalescedNodes and alias(v)=u
    private HashMap<Graph<ASMExprRT>.Node, Graph<ASMExprRT>.Node> alias;

    //chosen color for each node
    private HashMap<Graph<ASMExprRT>.Node, String> color;

    private ASMGraph cfg;
    private InterferenceGraph interference;
    private ASMLiveVariableDFA liveness;
    private List<ASMInstr> instrs;

    private int K;// number of usable registers

    private static List<String> allRegisters =
            Arrays.asList("rax", "rbx", "rcx", "rdx", "r8", "r9", "r10", "r11", "r12", "r13", "r14", "r15","rsi","rdi","rsp","rbp");


    public RegAllocationColoringVisitor(SpillMode s) {
        precolored = new HashSet<>();
        initial = new HashSet<>();
        simplifyWorklist = new HashSet<>();
        freezeWorklist = new HashSet<>();
        spillWorklist = new HashSet<>();
        spilledNodes = new HashSet<>();
        coalescedNodes = new HashSet<>();
        coloredNodes = new HashSet<>();
        selectStack = new Stack<>();

        coalescedMoves = new HashSet<>();
        constrainedMoves = new HashSet<>();
        frozenMoves = new HashSet<>();
        worklistMoves = new HashSet<>();
        activeMoves = new HashSet<>();

        adjSet = new HashSet<>();
        adjList = new HashMap<>();
        moveList = new HashMap<>();
        alias = new HashMap<>();
        color = new HashMap<>();
        degree = new HashMap<>();
        if (s == SpillMode.Reserve) {
            usableRegisters = Arrays.asList("rax", "rbx", "rcx", "rdx", "r8", "r9", "r10", "r11", "r12", "rsi", "rdi");
        } else {
            usableRegisters = Arrays.asList("rax", "rbx", "rcx", "rdx", "r8", "r9", "r10", "r11", "r12", "r13", "r14", "r15", "rsi", "rdi");
        }
        K = usableRegisters.size();
    }

    public void checkInvariants() {
        //check invariants
        String errors = "Violated Invariants: ";
        if (!checkSimplifyWorklistInv()) {
            errors += " simplify-worklist ";
        }
        if (!checkSpillWorklistInv()) {
            errors += " spill-worklist ";
        }
        if (!checkDegreeInv()) {
            errors += " degree ";
        }
        if (!checkFreezeWorklistInv()) {
            errors += " freeze-worklist ";
        }
        if (!errors.equals("Violated Invariants: ")){
            throw new InternalCompilerError(errors);
        }
    }

    public void checkEndInvariants() {
        //check invariants
        for (Graph<ASMExprRT>.Node n : interference.getAllNodes()){
            int c = 0;
            c = precolored.contains(n) ? c + 1 : c;
            c = initial.contains(n) ? c + 1 : c;
            c = spilledNodes.contains(n) ? c + 1 : c;
            c = spillWorklist.contains(n) ? c + 1 : c;
            c = freezeWorklist.contains(n) ? c + 1 : c;
            c = simplifyWorklist.contains(n) ? c + 1 : c;
            c = coalescedNodes.contains(n) ? c + 1 : c;
            c = selectStack.contains(n) ? c + 1 : c;
            c = coloredNodes.contains(n) ? c + 1 : c;
            if (c != 1) {
                String error = "Node " + n + " is in " + c + "worklists!";
                throw new InternalCompilerError(error);
            }
        }
    }

    public List<ASMInstr> allocate(List<ASMInstr> instrs) {
        this.instrs = instrs;
        livenessAnalysis();

        build();
        checkInvariants();

        makeWorkList();

        while (simplifyWorklist.size() != 0
                || worklistMoves.size() != 0
                || freezeWorklist.size() != 0
                || spillWorklist.size() != 0) {

            //run algorithm
            if (simplifyWorklist.size() != 0) {
                simplify();
            } else if (worklistMoves.size() != 0) {
                coalesce();
            } else if (freezeWorklist.size() != 0) {
                freeze();
            } else {
                selectSpill();
            }
        }
        assignColors();
        checkEndInvariants();
        return rewriteProgram(new ArrayList<>(spilledNodes));
    }

    private void livenessAnalysis() {
        cfg = new ASMGraph(instrs);
        liveness = new ASMLiveVariableDFA(cfg);
        liveness.runWorklistAlgo();
    }

    private void build() {
        interference = new InterferenceGraph();
        Map<Graph<ASMInstr>.Node, Set<ASMExprRT>> outs =
                liveness.getOutMap();

        //build interference graph
        for (ASMExprRT temp : getAllRT(instrs)) {
            //add nodes to interference graph
            if (!interference.checkTemp(temp)) {
                Graph<ASMExprRT>.Node node = interference.addNode(temp);
                degree.put(node, 0);
                adjList.put(node, new HashSet<>());
                moveList.put(node, new HashSet<>());
                //add nodes to correct initial set
                if (temp instanceof ASMExprReg) {
                    precolored.add(node);
                    color.put(node, ((ASMExprReg) temp).getReg());
                } else {
                    initial.add(node);
                }
            }
        }
        //for all the registers
        for (String r : allRegisters) {
            ASMExprReg reg = new ASMExprReg(r);
            //add nodes to interference graph
            if (!interference.checkTemp(reg)) {
                Graph<ASMExprRT>.Node node = interference.addNode(reg);
                degree.put(node, 0);
                adjList.put(node, new HashSet<>());
                moveList.put(node, new HashSet<>());
                precolored.add(node);
                color.put(node, r);
            }
        }

        //forall blocks b in program
        for (Graph<ASMInstr>.Node b : cfg.getAllNodes()) {
            //live = liveout(b)
            Set<ASMExprRT> live = outs.get(b);
            //forall instructions in b in reverse order (1 instruction in b for us)
            ASMInstr i = b.getT();
            //if ismoveinstr(i)
            if (isMoveInstruction(i)) {
                //live = live/use(i)
                live = new HashSet<>(Sets.difference(live, ASMLiveVariableDFA.use(b)));
                //forall t in def(i)+use(i)
                for (ASMExprRT t : new HashSet<>(
                        Sets.union(ASMLiveVariableDFA.def(b), ASMLiveVariableDFA.use(b)))
                ) {
                    //movelist[n] <- movelist[n] + {I}
                    //where n is the node associated with t
                    Graph<ASMExprRT>.Node n = interference.getNode(t);
                    moveList.get(n).add(i);
                }
                //worklistMoves = workListMoves + {I}
                worklistMoves.add(i);
            }
            //live = live + def(i)
            live = new HashSet<>(Sets.union(live, ASMLiveVariableDFA.def(b)));
            //forall d in def(i)
            for (ASMExprRT d : ASMLiveVariableDFA.def(b)) {
                //forall l in live
                for (ASMExprRT l : live) {
                    //addEdge(l,d)
                    addEdge(l, d);
                }
            }
        }
    }

    private void makeWorkList() {
        //forall n in initial
        for (Graph<ASMExprRT>.Node n : new HashSet<>(initial)) {
            //initial = initial\{n}
            initial.remove(n);
            //if degree[n] >= K
            if (degree.get(n) >= K) {
                //spillWorklist = spillWorklist + {n}
                spillWorklist.add(n);
            } else if (isMoveRelated(n)) {
                //freezeWorklist = freezeWorklist + {n}
                freezeWorklist.add(n);
            } else {
                //simplifyWorklist = simplifyWorklist + {n}
                simplifyWorklist.add(n);
            }
        }
    }

    private void simplify() {
        // let n be in simplifyWorklist
        // simplifyWorklist is guaranteed to be non-empty by a precondition!
        Graph<ASMExprRT>.Node n = simplifyWorklist.iterator().next();
        // simplifyWorklist = simplifyWorklist \ {n}
        simplifyWorklist.remove(n);
        // push n to selectStack
        selectStack.push(n);
        for (Graph<ASMExprRT>.Node m : getAdjacent(n)) {
            decrementDegree(m);
        }
    }

    private void coalesce() {
        // let m (= copy(x, y)) in worklistMoves
        // here, we use move instead of m
        ASMInstr_2Arg move = null;
        for (ASMInstr instr : worklistMoves) {
            if (isCopy(instr)) {
                move = (ASMInstr_2Arg) instr;
                break;
            }
        }
        // can't do anything
        if (move == null) return;

        ASMExprRT x = (ASMExprRT) move.getDest();
        ASMExprRT y = (ASMExprRT) move.getSrc();
        // x <- GetAlias(x)
        Graph<ASMExprRT>.Node xNode = getAlias(interference.getNode(x));
        // y <- GetAlias(y)
        Graph<ASMExprRT>.Node yNode = getAlias(interference.getNode(y));

        Graph<ASMExprRT>.Node u;
        Graph<ASMExprRT>.Node v;
        // if y in precolored
        if (precolored.contains(yNode)) {
            // let (u, v) = (y, x)
            u = yNode;
            v = xNode;
        } else {
            // let (u, v) = (x, y)
            u = xNode;
            v = yNode;
        }
        // worklistMoves <- worklistMoves \ {m}
        worklistMoves.remove(move);

        if (u.equals(v)) { // if u == v
            // coalescedMoves <- coalescedMoves + {m}
            coalescedMoves.add(move);
            addWorkList(u);
        } else if (precolored.contains(v) || adjSet.contains(new Pair<>(u, v))) {
            // else if v in precolored OR (u, v) in adjSet
            // constrainedMoves <- constrainedMoves + {m}
            constrainedMoves.add(move);
            addWorkList(u);
            addWorkList(v);
        } else if (
                (
                        precolored.contains(u)
                                && getAdjacent(v).stream().allMatch(t -> ok(t, u))
                ) || (
                        !precolored.contains(u)
                                && conservative(new HashSet<>(Sets.union(getAdjacent(u), getAdjacent(v))))
                )
        ) {
            // else if u in precolored AND (\forall t in Adjacent(v), OK(t, u))
            // OR u \not in precolored AND Conservative(Adjacent(u) union Adjacent(v))

            // coalescedMoves <- coalescedMoves + {m}
            coalescedMoves.add(move);
            combine(u, v);
            addWorkList(u);
        } else {
            // activeMoves <- activeMoves + {m}
            activeMoves.add(move);
        }
    }

    private void combine(Graph<ASMExprRT>.Node u,
                         Graph<ASMExprRT>.Node v) {
        if (freezeWorklist.contains(v)) { // if v in freezeWorklist
            // freezeWorklist <- freezeWorklist \ {v}
            freezeWorklist.remove(v);
        } else if (spillWorklist.contains(v)){
            // spillWorklist <- spillWorklist \ {v}
            spillWorklist.remove(v);
        } else {
            throw new InternalCompilerError("invariant violated");
        }

        // coalescedNodes <- coalescedNodes union {v}
        coalescedNodes.add(v);
        // alias[v] <- u
        alias.put(v, u);
        // moveList[u] <- moveList[u] union moveList[v]
        moveList.get(u).addAll(moveList.get(v));
        // EnableMoves(v)
        enableMoves(v);
        // forall t in Adjacent(v)
        for (Graph<ASMExprRT>.Node t : getAdjacent(v)) {
            ASMExprRT tTemp = interference.getTemp(t);
            ASMExprRT uTemp = interference.getTemp(u);
            addEdge(tTemp, uTemp);
            decrementDegree(t);
        }
        // if degree[u] >= K AND u in freezeWorklist
        if (degree.get(u) >= K && freezeWorklist.contains(u)) {
            // freezeWorklist <- freezeWorklist \ {u}
            freezeWorklist.remove(u);
            // spillWorklist <- spillWorklist union {u}
            spillWorklist.add(u);
        }
    }

    private void freeze() {
        // let u in freezeWorklist
        Graph<ASMExprRT>.Node u = freezeWorklist.iterator().next();
        // freezeWorklist <- freezeWorklist \ {u}
        freezeWorklist.remove(u);
        // simplifyWorklist <- simplifyWorklist union {u}
        simplifyWorklist.add(u);
        // FreezeMoves(u)
        freezeMoves(u);
    }

    private void freezeMoves(Graph<ASMExprRT>.Node u) {
        // forall m (= copy(x, y)) in NodeMoves(u)
        for (ASMInstr instr : nodeMoves(u)) {
            ASMInstr_2Arg move = (ASMInstr_2Arg) instr;
            if (!isCopy(move)) return;

            Graph<ASMExprRT>.Node x = interference.getNode((ASMExprRT) move.getDest());
            Graph<ASMExprRT>.Node y = interference.getNode((ASMExprRT) move.getSrc());

            Graph<ASMExprRT>.Node v;
            // if GetAlias(y) = GetAlias(u)
            if (getAlias(y).equals(getAlias(u))) {
                // v <- GetAlias(x)
                v = getAlias(x);
            } else {
                // v <- GetAlias(y)
                v = getAlias(y);
            }

            // activeMoves <- activeMoves \ {m}
            activeMoves.remove(move);
            // frozenMoves <- frozenMoves union {m}
            frozenMoves.add(move);

            // if v in freezeWorklist AND NodeMoves(v) = {}
            if (freezeWorklist.contains(v) && nodeMoves(v).isEmpty()) {
                // freezeWorklist <- freezeWorklist \ {v}
                freezeWorklist.remove(v);
                // simplifyWorklist <- simplifyWorklist union {v}
                simplifyWorklist.add(v);
            }
        }
    }

    private void selectSpill() {
        //TODO heuristic to choose nodes with large live-ranges
        Graph<ASMExprRT>.Node m = spillWorklist.iterator().next();
        spillWorklist.remove(m);
        simplifyWorklist.add(m);
        freezeMoves(m);
    }

    private void assignColors() {
        // while SelectStack not empty
        while (!selectStack.empty()) {
            // let n = pop(SelectStack)
            Graph<ASMExprRT>.Node n = selectStack.pop();
            // okColors = {usable registers}
            Set<String> okColors = new HashSet<>(usableRegisters);

            // forall w in adjList[n]
            for (Graph<ASMExprRT>.Node w : adjList.get(n)) {
                // if getalias(w) in (coloredNodes + precolored)
                if (Sets.union(coloredNodes, precolored)
                        .contains(getAlias(w))) {
                    // okColors = okColors \ {color[getAlias(w)]}
                    okColors.remove(color.get(getAlias(w)));
                }
            }
            if (okColors.isEmpty()) {
                spilledNodes.add(n);
            } else {
                coloredNodes.add(n);
                // let c in okColors
                String c = okColors.iterator().next();
                // color[n] <- c
                color.put(n, c);
            }
        }

        // color coalesced nodes according to their alias
        for (Graph<ASMExprRT>.Node n : new HashSet<>(coalescedNodes)) {
            if (spilledNodes.contains(getAlias(n))){
                coalescedNodes.remove(n);
                spilledNodes.add(n);
            } else {
                color.put(n, color.get(getAlias(n)));
            }
        }
    }

    private List<ASMInstr> rewriteProgram(List<Graph<ASMExprRT>.Node> spilledNodes) {
        List<ASMInstr> new_instrs = new ArrayList<>();
        Set<Graph<ASMExprRT>.Node> spills = new HashSet<>(spilledNodes);
        for (ASMInstr i : instrs) {
            ASMInstr new_instr = rewriteInstr(i, spills);
            if (new_instr != null) new_instrs.add(new_instr);
        }
        //no need to reset data structures bc we only run coloring once
        return new_instrs;
    }

    //HELPERS DOWN HERE
    private void addEdge(ASMExprRT u, ASMExprRT v) {
        //if (u,v) not in adjSet and u != v
        Graph<ASMExprRT>.Node uNode = interference.getNode(u);
        Graph<ASMExprRT>.Node vNode = interference.getNode(v);
        if (!adjSet.contains(new Pair<>(uNode, vNode)) && !u.equals(v)) {
            //adjSet = adjSet + {(u,v), (v,u)}
            adjSet.add(new Pair<>(uNode, vNode));
            adjSet.add(new Pair<>(vNode, uNode));

            //if u not in precolored
            if (!precolored.contains(uNode)) {
                //adjList[u] = adjList[u] + {v}
                adjList.get(uNode).add(vNode);
                //degree[u] = degree[u] + 1
                degree.put(uNode, degree.get(uNode) + 1);
            }
            //same for v
            if (!precolored.contains(vNode) && !u.equals(v)) {
                adjList.get(vNode).add(uNode);
                degree.put(vNode, degree.get(vNode) + 1);
            }
        }

    }

    private Set<Graph<ASMExprRT>.Node> getAdjacent(Graph<ASMExprRT>.Node n) {
        //adjList[n]\(activeMoves + coalescedNodes)
        return new HashSet<>(Sets.difference(
                adjList.get(n),
                Sets.union(new HashSet<>(selectStack), coalescedNodes)
        ));
    }

    private Set<ASMInstr> getNodeMoves(Graph<ASMExprRT>.Node n) {
        //moveList[n] intersection (activeMoves + worklistMoves)
        return new HashSet<>(Sets.intersection(
                moveList.get(n),
                Sets.union(activeMoves, worklistMoves)
        ));
    }

    private boolean isMoveRelated(Graph<ASMExprRT>.Node n) {
        return getNodeMoves(n).size() != 0;
    }

    private void decrementDegree(Graph<ASMExprRT>.Node m) {
        //let d = degree[m]
        int d = degree.get(m);
        //degree[m] = d-1
        degree.put(m, d - 1);
        //if d == K
        if (d == K) {
            //enableMoves({m} + adjacent(m))
            Set<Graph<ASMExprRT>.Node> s = new HashSet<>();
            s.add(m);
            enableMoves(new HashSet<>(Sets.union(s, getAdjacent(m))));
            spillWorklist.remove(m);
            if (isMoveRelated(m)) {
                freezeWorklist.add(m);
            } else {
                simplifyWorklist.add(m);
            }
        }
    }

    private void enableMoves(Set<Graph<ASMExprRT>.Node> nodes) {
        nodes.forEach(this::enableMoves);
    }

    private void enableMoves(Graph<ASMExprRT>.Node n) {
        for (ASMInstr m : nodeMoves(n)) {
            if (activeMoves.contains(m)) {
                activeMoves.remove(m);
                worklistMoves.add(m);
            }
        }
    }

    private Set<ASMInstr> nodeMoves(Graph<ASMExprRT>.Node n) {
        return new HashSet<>(Sets.intersection(
                moveList.get(n),
                Sets.union(activeMoves, worklistMoves)
        ));
    }

    private void addWorkList(Graph<ASMExprRT>.Node u) {
        if (!precolored.contains(u) && !(isMoveRelated(u)) && degree.get(u) < K) {
            freezeWorklist.remove(u);
            simplifyWorklist.add(u);
        }
    }

    private boolean ok(Graph<ASMExprRT>.Node t,
                       Graph<ASMExprRT>.Node r) {
        return degree.get(t) < K
                || precolored.contains(t)
                || adjSet.contains(new Pair<>(t, r));
    }

    private boolean conservative(Set<Graph<ASMExprRT>.Node> nodes) {
        int k = 0;
        for (Graph<ASMExprRT>.Node n : nodes) {
            if (degree.get(n) >= K) {
                k++;
            }
        }
        return k < K;
    }

    private Graph<ASMExprRT>.Node getAlias(Graph<ASMExprRT>.Node n) {
        if (coalescedNodes.contains(n)) {
            return getAlias(alias.get(n));
        }
        return n;
    }

    private ASMInstr rewriteInstr(ASMInstr i, Set<Graph<ASMExprRT>.Node> spilledNodes) {
//        System.out.println("_" + i);
        if (i instanceof ASMInstr_1Arg) {
            ASMInstr_1Arg i1 = (ASMInstr_1Arg) i;
            return new ASMInstr_1Arg(i1.getOpCode(), rewriteExpr(i1.getArg(), spilledNodes));
        } else if (i instanceof ASMInstr_2Arg) {
            ASMInstr_2Arg i2 = (ASMInstr_2Arg) i;
            ASMExpr dest = rewriteExpr(i2.getDest(), spilledNodes);
            ASMExpr src = rewriteExpr(i2.getSrc(), spilledNodes);
            if (dest.equals(src) &&
                    (i.getOpCode() == ASMOpCode.MOV || i.getOpCode() == ASMOpCode.MOVZX)
            ){
                return null;
            } else {
                return new ASMInstr_2Arg(i2.getOpCode(),dest,src);
            }
        } else {
            return i;
        }
    }

    private ASMExpr rewriteExpr(ASMExpr e, Set<Graph<ASMExprRT>.Node> spilledNodes) {
        if (e instanceof ASMExprMem) {
            ASMExprMem m = (ASMExprMem) e;
            return new ASMExprMem(rewriteExpr(m.getAddr(), spilledNodes));
        } else if (e instanceof ASMExprTemp) {
            ASMExprTemp m = (ASMExprTemp) e;
            Graph<ASMExprRT>.Node n = interference.getNode(m);
            if (spilledNodes.contains(n)) {
                return e;
            } else {
                //replace temp with reg
                String c = color.get(n);
                return new ASMExprReg(c);
            }
        } else if (e instanceof ASMExprBinOpAdd) {
            ASMExprBinOp b = (ASMExprBinOpAdd) e;
            return new ASMExprBinOpAdd(
                    rewriteExpr(b.getLeft(), spilledNodes),
                    rewriteExpr(b.getRight(), spilledNodes)
            );
        } else if (e instanceof ASMExprBinOpMult) {
            ASMExprBinOp b = (ASMExprBinOpMult) e;
            return new ASMExprBinOpMult(
                    rewriteExpr(b.getLeft(), spilledNodes),
                    rewriteExpr(b.getRight(), spilledNodes)
            );
        } else {
            return e;
        }
    }

    private boolean isMoveInstruction(ASMInstr i) {
        return isCopy(i);
        //return i.destIsDefButNoUse();
    }

    //GETTERS (for testing)
    public ASMGraph getCfg() {
        return cfg;
    }

    public InterferenceGraph getInterference() {
        return interference;
    }

    public ASMLiveVariableDFA getLiveness() {
        return liveness;
    }

    public List<ASMInstr> getInstrs() {
        return instrs;
    }

    public int getK() {
        return K;
    }

    //FOR TESTING ONLY!
    //USED FOR TESTING ONLY - stops after build graph step so we can test for invariants
    public void buildInterferenceGraph(List<ASMInstr> instrs) {
        this.instrs = instrs;
        livenessAnalysis();
        build();
    }

    //check invariants which are true after build step

    public boolean checkDegreeInv() {
        //s = simplifyWL + freezeWL + spillWL
        HashSet<Graph<ASMExprRT>.Node> s = new HashSet<>();
        s.addAll(simplifyWorklist);
        s.addAll(freezeWorklist);
        s.addAll(spillWorklist);
        for (Graph<ASMExprRT>.Node u : s) {
            HashSet<Graph<ASMExprRT>.Node> s2 = new HashSet<>();
            s2.addAll(s);
            s2.addAll(precolored);
            //s2 = adjList(u) intersection (precolored + simplifyWL + freezeWL + spillWL)
            HashSet<Graph<ASMExprRT>.Node> s3 = new HashSet<>(Sets.intersection(s2, adjList.get(u)));
            if (degree.get(u) != s3.size()) {
                return false;
            }
        }
        return true;
    }

    public boolean checkSimplifyWorklistInv() {
        for (Graph<ASMExprRT>.Node u : simplifyWorklist) {
            if (degree.get(u) >= K) {
                return false;
            }
            //movelist[u] intersection (activeMoves + worklistMoves)
            Set<ASMInstr> s = new HashSet<>();
            s.addAll(activeMoves);
            s.addAll(worklistMoves);
            Set<ASMInstr> s_ = new HashSet<>(Sets.intersection(moveList.get(u), s));
            if (s_.size() != 0) {
                return false;
            }
        }
        return true;
    }

    public boolean checkFreezeWorklistInv() {
        for (Graph<ASMExprRT>.Node u : freezeWorklist) {
            if (degree.get(u) >= K) {
                return false;
            }
            //movelist[u] intersection (activeMoves + worklistMoves)
            Set<ASMInstr> s = new HashSet<>();
            s.addAll(activeMoves);
            s.addAll(worklistMoves);
            s = new HashSet<>(Sets.intersection(moveList.get(u), s));
            if (s.size() == 0) {
                return false;
            }
        }
        return true;
    }

    public boolean checkSpillWorklistInv() {
        for (Graph<ASMExprRT>.Node u : spillWorklist) {
            if (degree.get(u) < K) {
                return false;
            }
        }
        return true;
    }

    //return true if instruction is COPY(x,y)
    public boolean isCopy(ASMInstr instr){
        if (instr instanceof ASMInstr_2Arg) {
            ASMInstr_2Arg i = (ASMInstr_2Arg) instr;
            boolean checkOpCode = (i.getOpCode() == ASMOpCode.MOV);
            return (checkOpCode && i.getDest() instanceof ASMExprRT && i.getSrc() instanceof ASMExprRT);
        }
        return false;
    }

    //get set of registers and temps
    public Set<ASMExprRT> getAllRT(List<ASMInstr> instrs) {
        Set<ASMExprRT> temps = new HashSet<>();
        for (ASMInstr i : instrs) {
            if (i instanceof ASMInstr_1Arg) {
                temps.addAll(getRTExpr(((ASMInstr_1Arg) i).getArg()));
            } else if (i instanceof ASMInstr_2Arg) {
                ASMInstr_2Arg i2 = (ASMInstr_2Arg) i;
                temps.addAll(getRTExpr(i2.getDest()));
                temps.addAll(getRTExpr(i2.getSrc()));
            }
        }
        return temps;
    }

    private Set<ASMExprRT> getRTExpr(ASMExpr e) {
        Set<ASMExprRT> temps = new HashSet<>();
        if (e instanceof ASMExprBinOp) {
            ASMExprBinOp b = (ASMExprBinOp) e;
            temps.addAll(getRTExpr(b.getLeft()));
            temps.addAll(getRTExpr(b.getRight()));
        } else if (e instanceof ASMExprTemp) {
            temps.add((ASMExprTemp) e);
        } else if (e instanceof ASMExprReg) {
            temps.add((ASMExprReg) e);
        } else if (e instanceof ASMExprMem) {
            temps.addAll(getRTExpr(((ASMExprMem) e).getAddr()));
        }
        return temps;
    }
}
