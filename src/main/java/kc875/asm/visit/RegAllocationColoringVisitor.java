package kc875.asm.visit;

import com.google.common.collect.Sets;
import kc875.asm.*;
import kc875.asm.dfa.ASMGraph;
import kc875.asm.dfa.InterferenceGraph;
import kc875.asm.dfa.LiveVariableDFA;
import kc875.cfg.Graph;
import polyglot.util.Pair;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class RegAllocationColoringVisitor {
    //every node is in exactly one of these sets
    private HashSet<Graph<ASMExprRegReplaceable>.Node> precolored; // machine registers, pre-assigned
    private HashSet<Graph<ASMExprRegReplaceable>.Node> initial; // temps, not processed yet
    private HashSet<Graph<ASMExprRegReplaceable>.Node> simplifyWorklist; //low degree, non move related nodes
    private HashSet<Graph<ASMExprRegReplaceable>.Node> freezeWorklist;//low degree, move related nodes
    private HashSet<Graph<ASMExprRegReplaceable>.Node> spillWorklist;//high degree nodes
    private HashSet<Graph<ASMExprRegReplaceable>.Node> spilledNodes;//marked for spilling this round, initially empty
    private HashSet<Graph<ASMExprRegReplaceable>.Node> coalescedNodes;//registers which are coalesced
    //u <- v coalesced then v is added to this set, u is moved to work list
    private HashSet<Graph<ASMExprRegReplaceable>.Node> coloredNodes; //successfully colored
    private Stack<Graph<ASMExprRegReplaceable>.Node> selectStack; //stacks w/ temps removed from graph

    //every move is in exactly one of these sets
    private HashSet<ASMInstr> coalescedMoves; //coalesced moves
    private HashSet<ASMInstr> constrainedMoves; //moves whose src/target interfere
    private HashSet<ASMInstr> frozenMoves; //moves no longer considered for coalescing
    private HashSet<ASMInstr> worklistMoves; //moves enabled for possible coalescing
    private HashSet<ASMInstr> activeMoves; //moves not ready for coalescing

    //interference graph abstraction
    //set of interference edges - reverse edges must always be in the set
    private HashSet<Pair<Graph<ASMExprRegReplaceable>.Node, Graph<ASMExprRegReplaceable>.Node>> adjSet;

    //list repr of the graph, node -> list of interfering nodes
    private HashMap<Graph<ASMExprRegReplaceable>.Node, Set<Graph<ASMExprRegReplaceable>.Node>> adjList;

    //Degree of each node
    private HashMap<Graph<ASMExprRegReplaceable>.Node, Integer> degree;

    //map from node -> list of moves it is associated with
    private HashMap<Graph<ASMExprRegReplaceable>.Node, HashSet<ASMInstr>> moveList;

    //alias - when MOV(u,v) coalesced, then v is in coalescedNodes and alias(v)=u
    private HashMap<Graph<ASMExprRegReplaceable>.Node, Graph<ASMExprRegReplaceable>.Node> alias;

    //chosen color for each node
    private HashMap<Graph<ASMExprRegReplaceable>.Node, Integer> color;

    private ASMGraph cfg;
    private InterferenceGraph interference;
    private LiveVariableDFA liveness;
    private List<ASMInstr> instrs;

    private int K = 16;//num regs I think

    RegAllocationColoringVisitor() {
    }

    private void allocate(List<ASMInstr> instrs) {
        this.instrs = instrs;
        //TODO
        // initialization goes here because allocate is recursive
        // some data structures may not need to be reset,
        // in which case they can be moved up to the constructor
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

        livenessAnalysis(instrs);
        build();
        makeWorkList();
        while (simplifyWorklist.size() != 0
                || worklistMoves.size() != 0
                || freezeWorklist.size() != 0
                || spillWorklist.size() != 0) {
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
        if (spilledNodes.size() != 0) {
            List<ASMInstr> new_program = rewriteProgram(new ArrayList<>(spilledNodes));
            allocate(new_program);
        }
    }

    private void livenessAnalysis(List<ASMInstr> instrs) {
        cfg = new ASMGraph(instrs);
        liveness = new LiveVariableDFA(cfg);
        //TODO run LVA
    }

    private void build() {
        //TODO initialize interference graph
        interference = new InterferenceGraph();
        //foralll blocks b in program
        for (Graph<ASMInstr>.Node b : cfg.getAllNodes()) {
            //live = liveout(b)
            Map<Graph<ASMInstr>.Node, Set<ASMExprRegReplaceable>> outs =
                    liveness.getOutMap();
            Set<ASMExprRegReplaceable> live = outs.get(b);
            for (ASMExprRegReplaceable temp : live){
                if (!interference.checkTemp(temp)){
                    Graph<ASMExprRegReplaceable>.Node node = interference.addNode(temp);
                    degree.put(node, 0);
                    adjList.put(node, new HashSet<>());
                }
            }
            //forall instructions in b in reverse order (1 instruction in b for us)
            ASMInstr i = b.getT();
            //if ismoveinstr(i)
            if (i.hasNewDef()) {
                //live = live/use(i)
                live = Sets.difference(live, LiveVariableDFA.use(b));
                //forall n in def(i)+use(i)
                for (ASMExprRegReplaceable n : new HashSet<>(
                        Sets.union(LiveVariableDFA.def(b), LiveVariableDFA.use(b)))
                ) {
                    //movelist[n] <- movelist[n] + {I}
                    moveList.get(interference.getNode(n)).add(i);
                }
                //worklistMoves = workListMoves + {I}
                worklistMoves.add(i);
            }
            //live = live + def(i)
            live = new HashSet<>(Sets.union(live, LiveVariableDFA.def(b)));
            //forall d in def(i)
            for (ASMExprRegReplaceable d : LiveVariableDFA.def(b)) {
                //forall l in live
                for (ASMExprRegReplaceable l : live) {
                    //addEdge(l,d)
                    addEdge(l, d);
                }
            }
            //live = use(i)+ (live\def(i)
            live = new HashSet<>(Sets.union(LiveVariableDFA.use(b), Sets.difference(live, LiveVariableDFA.def(b))));
            //TODO what do we do with live? store it back into liveness outmap?
        }
    }

    private void makeWorkList() {
        //forall n in initial
        for (Graph<ASMExprRegReplaceable>.Node n : initial) {
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
        //TODO selecting things from worklist
        //let n be in simplifyWorklist
        Graph<ASMExprRegReplaceable>.Node n = new ArrayList<>(simplifyWorklist).get(0);
        //simplifyWorklist = simplifyWorklist \ {n}
        simplifyWorklist.remove(n);
        //push n to selectStack
        selectStack.push(n);
        for (Graph<ASMExprRegReplaceable>.Node m : getAdjacent(n)) {
            decrementDegree(m);
        }
    }

    private void coalesce() {
        //TODO: implement the invariant that all of these are moves

        // let m (= copy(x, y)) \in worklistMoves
        // here, we use move instead of m
        ASMInstr_2Arg move = null;
        ASMExprTemp x = null;
        ASMExprTemp y = null;
        for (ASMInstr instr : worklistMoves) {
            move = (ASMInstr_2Arg) instr;
            ASMExpr xE = move.getSrc();
            ASMExpr yE = move.getDest();

            if (xE instanceof ASMExprTemp && yE instanceof ASMExprTemp) {
                x = (ASMExprTemp) xE;
                y = (ASMExprTemp) yE;
            }
        }

        if (move == null || x == null) return; // TODO

        // x <- GetAlias(x)
        Graph<ASMExprRegReplaceable>.Node xNode = getAlias(interference.getNode(x));
        // y <- GetAlias(y)
        Graph<ASMExprRegReplaceable>.Node yNode = getAlias(interference.getNode(y));

        Graph<ASMExprRegReplaceable>.Node u;
        Graph<ASMExprRegReplaceable>.Node v;
        // if y \in precolored
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
            // coalescedMoves <- coalescedMoves \union {m}
            coalescedMoves.add(move);
            // AddWorkList(u)
            addWorkList(u);
        } else if (precolored.contains(v) || adjSet.contains(new Pair<>(u, v))) {
            // else if v \in precolored OR (u, v) \in adjSet

            // constrainedMoves <- constrainedMoves \union {m}
            constrainedMoves.add(move);
            // AddWorkList(u)
            addWorkList(u);
            // AddWorkList(v)
            addWorkList(v);
        } else if (
                (
                        precolored.contains(u)
                                && getAdjacent(v).stream().allMatch(t -> ok(t, u))
                ) || (
                        !precolored.contains(u)
                                && conservative(Sets.union(getAdjacent(u), getAdjacent(v)))
                )
        ) {
            // else if u \in precolored AND (\forall t \in Adjacent(v), OK(t, u))
            // OR u \not \in precolored AND Conservative(Adjacent(u) \union Adjacent(v))

            // coalescedMoves <- coalescedMoves \union {m}
            coalescedMoves.add(move);
            // Combine(u, v)
            combine(u, v);
            // AddWorkList(u)
            addWorkList(u);
        } else {
            // activeMoves <- activeMoves \union {m}
            activeMoves.add(move);
        }
    }

    private void combine(Graph<ASMExprRegReplaceable>.Node u,
                         Graph<ASMExprRegReplaceable>.Node v) {
        if (freezeWorklist.contains(v)) { // if v \in freezeWorklist
            // freezeWorklist <- freezeWorklist \ {v}
            freezeWorklist.remove(v);
        } else {
            // spillWorklist <- spillWorklist \ {v}
            spillWorklist.remove(v);
        }

        // coalescedNodes <- coalescedNodes \union {v}
        coalescedNodes.add(v);
        // alias[v] <- u
        alias.put(v, u);
        // moveList[u] <- moveList[u] \union moveList[v]
        moveList.get(u).addAll(moveList.get(v));
        // EnableMoves(v)
        enableMoves(v);

        // forall t \in Adjacent(v)
        for (Graph<ASMExprRegReplaceable>.Node t : getAdjacent(v)) {
            ASMExprRegReplaceable tInstr = interference.getTemp(t);
            ASMExprRegReplaceable uInstr = interference.getTemp(u);

            // AddEdge(t, u)
            addEdge(tInstr, uInstr);
            // DecrementDegree(t)
            decrementDegree(t);
        }

        // if degree[u] >= K AND u \in freezeWorklist
        if (degree.get(u) >= K && freezeWorklist.contains(u)) {
            // freezeWorklist <- freezeWorklist \ {u}
            freezeWorklist.remove(u);
            // spillWorklist <- spillWorklist \union {u}
            spillWorklist.add(u);
        }
    }

    private void freeze() {
        // let u \in freezeWorklist
        Graph<ASMExprRegReplaceable>.Node u = freezeWorklist.iterator().next();
        // freezeWorklist <- freezeWorklist \ {u}
        freezeWorklist.remove(u);
        // simplifyWorklist <- simplifyWorklist \union {u}
        simplifyWorklist.add(u);
        // FreezeMoves(u)
        freezeMoves(u);
    }

    private void freezeMoves(Graph<ASMExprRegReplaceable>.Node u) {
        // forall m (= copy(x, y)) \in NodeMoves(u)
        for (ASMInstr instr : worklistMoves) {
            ASMInstr_2Arg move = (ASMInstr_2Arg) instr;
            ASMExpr xE = move.getSrc();
            ASMExpr yE = move.getDest();

            if (xE instanceof ASMExprTemp && yE instanceof ASMExprTemp) {
                Graph<ASMExprRegReplaceable>.Node x = interference.getNode((ASMExprTemp) xE);
                Graph<ASMExprRegReplaceable>.Node y = interference.getNode((ASMExprTemp) yE);

                Graph<ASMExprRegReplaceable>.Node v;


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
                // frozenMoves <- frozenMoves \union {m}
                frozenMoves.add(move);

                // if v \in freezeWorklist AND NodeMoves(v) = {}
                if (freezeWorklist.contains(v) && nodeMoves(v).isEmpty()) {
                    // freezeWorklist <- freezeWorklist \ {v}
                    freezeWorklist.remove(v);
                    // simplifyWorklist <- simplifyWorklist \union {v}
                    simplifyWorklist.add(v);
                }
            }
        }
    }

    private void selectSpill() {
        //TODO
    }

    private void assignColors() {
        while (!selectStack.empty()) {
            Graph<ASMExprRegReplaceable>.Node n = selectStack.pop();
            //okColors = {0,...K-1}
            //TODO right now colors are ints
            Set<Integer> okColors = IntStream.range(0, K)
                    .boxed()
                    .collect(Collectors.toSet());
            for (Graph<ASMExprRegReplaceable>.Node w : adjList.get(n)) {
                //if getalias(w) in (coloredNodes + precolored)
                if (Sets.union(coloredNodes, precolored)
                        .contains(getAlias(w))) {
                    //okColors = okColors\{color[getAlias(w)]}
                    okColors.remove(color.get(getAlias(w)));
                }
            }
            if (okColors.isEmpty()) {
                spilledNodes.add(n);
            } else {
                coloredNodes.add(n);
                //assign n to one of the ok colors
                int c = new ArrayList<>(okColors).get(0);
                color.put(n, c);
            }
        }
        //color coalesced nodes according to their alias
        for (Graph<ASMExprRegReplaceable>.Node n : coalescedNodes) {
            color.put(n, color.get(getAlias(n)));
        }
    }

    private List<ASMInstr> rewriteProgram(List<Graph<ASMExprRegReplaceable>.Node> spilledNodes) {
        //TODO
        return null;
    }

    //HELPERS DOWN HERE
    private void addEdge(ASMExprRegReplaceable u, ASMExprRegReplaceable v) {
        //if (u,v) not in adjSet and u != v
        Graph<ASMExprRegReplaceable>.Node uNode = interference.getNode(u);
        Graph<ASMExprRegReplaceable>.Node vNode = interference.getNode(v);
        if (!adjSet.contains(new Pair<>(uNode, vNode)) && !u.equals(v)) {
            //adjSet = adjSet + {(u,v), (v,u)}
            adjSet.add(new Pair<>(uNode, vNode));
            adjSet.add(new Pair<>(vNode, uNode));
        }
        //if u not in precolored
        if (!precolored.contains(uNode)) {
            //adjList[u] = adjList[u] + {v}
            adjList.get(uNode).add(vNode);
            //degree[u] = degree[u] + 1
            degree.put(uNode, degree.get(uNode) + 1);
        }
        //same for v
        if (!precolored.contains(vNode)) {
            adjList.get(vNode).add(uNode);
            degree.put(vNode, degree.get(vNode) + 1);
        }
    }

    private Set<Graph<ASMExprRegReplaceable>.Node> getAdjacent(Graph<ASMExprRegReplaceable>.Node n) {
        //adjList[n]\(activeMoves + coalescedNodes)
        return new HashSet<>(Sets.difference(
                adjList.get(n),
                Sets.union(new HashSet<>(selectStack), coalescedNodes)
        ));
    }

    private Set<ASMInstr> getNodeMoves(Graph<ASMExprRegReplaceable>.Node n) {
        //moveList[n] intersection (activeMoves + worklistMoves)
        return new HashSet<>(Sets.intersection(
                moveList.get(n),
                Sets.union(activeMoves, worklistMoves)
        ));
    }

    private boolean isMoveRelated(Graph<ASMExprRegReplaceable>.Node n) {
        return getNodeMoves(n).size() != 0;
    }

    private void decrementDegree(Graph<ASMExprRegReplaceable>.Node m) {
        //let d = degree[m]
        int d = degree.get(m);
        //degree[m] = d-1
        degree.put(m, d - 1);
        //if d == K
        if (d == K) {
            //enableMoves({m} + adjacent(m))
            Set<Graph<ASMExprRegReplaceable>.Node> s = new HashSet<>();
            s.add(m);
            enableMoves(new HashSet<>(Sets.union(s, getAdjacent(m))));
            spillWorklist.remove(m);
        }
        if (isMoveRelated(m)) {
            freezeWorklist.add(m);
        } else {
            simplifyWorklist.add(m);
        }
    }

    private void enableMoves(Set<Graph<ASMExprRegReplaceable>.Node> nodes) {
        nodes.forEach(this::enableMoves);
    }

    private void enableMoves(Graph<ASMExprRegReplaceable>.Node n) {
        for (ASMInstr m : nodeMoves(n)) {
            if (activeMoves.contains(m)) {
                activeMoves.remove(m);
                worklistMoves.add(m);
            }
        }
    }

    private Set<ASMInstr> nodeMoves(Graph<ASMExprRegReplaceable>.Node n) {
        return Sets.intersection(
                moveList.get(n),
                Sets.union(activeMoves, worklistMoves)
        );
    }

    private void addWorkList(Graph<ASMExprRegReplaceable>.Node u) {
        if (!precolored.contains(u) && !(isMoveRelated(u)) && degree.get(u) < K) {
            freezeWorklist.remove(u);
            simplifyWorklist.add(u);
        }
    }

    private boolean ok(Graph<ASMExprRegReplaceable>.Node t,
                       Graph<ASMExprRegReplaceable>.Node r) {
        return degree.get(t) < K
                && precolored.contains(t)
                && adjSet.contains(new Pair<>(t, r));
    }

    private boolean conservative(Set<Graph<ASMExprRegReplaceable>.Node> nodes) {
        int k = 0;
        for (Graph<ASMExprRegReplaceable>.Node n : nodes) {
            if (degree.get(n) >= K) {
                k = k + 1;
            }
        }
        return k < K;
    }

    private Graph<ASMExprRegReplaceable>.Node getAlias(Graph<ASMExprRegReplaceable>.Node n) {
        if (coalescedNodes.contains(n)) {
            return getAlias(alias.get(n));
        }
        return n;
    }
}
