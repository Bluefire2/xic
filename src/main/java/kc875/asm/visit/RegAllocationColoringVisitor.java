package kc875.asm.visit;

import kc875.asm.ASMExprRegReplaceable;
import kc875.asm.ASMInstr;
import kc875.asm.dfa.ASMGraph;
import kc875.asm.dfa.InterferenceGraph;
import kc875.asm.dfa.LiveVariableDFA;
import kc875.cfg.Graph;
import com.google.common.collect.Sets;
import polyglot.util.Pair;

import java.util.*;

public class RegAllocationColoringVisitor {
    //every node is in exactly one of these sets
    HashSet<Graph.Node> precolored; // machine registers, pre-assigned
    HashSet<Graph.Node> initial; // temps, not processed yet
    HashSet<Graph.Node> simplifyWorklist; //low degree, non move related nodes
    HashSet<Graph.Node> freezeWorklist;//low degree, move related nodes
    HashSet<Graph.Node> spillWorklist;//high degree nodes
    HashSet<Graph.Node> spilledNodes;//marked for spilling this round, initially empty
    HashSet<Graph.Node> coalescedNodes;//registers which are coalesced
    //u <- v coalesced then v is added to this set, u is moved to work list
    HashSet<Graph.Node> coloredNodes; //successfully colored
    Stack<Graph.Node> selectStack; //stacks w/ temps removed from graph

    //every move is in exactly one of these sets
    HashSet<ASMInstr> coalescedMoves; //coalesced moves
    HashSet<ASMInstr> constrainedMoves; //moves whose src/target interfere
    HashSet<ASMInstr> frozenMoves; //moves no longer considered for coalescing
    HashSet<ASMInstr> worklistMoves; //moves enabled for possible coalescing
    HashSet<ASMInstr> activeMoves; //moves not ready for coalescing

    //set of interference edges - reverse edges must always be in the set
    HashSet<Pair<Graph.Node, Graph.Node>> adjSet;

    //list repr of the graph, node -> list of interfering nodes
    HashMap<Graph.Node, Set<Graph.Node>> adjList;

    //Degree of each node
    HashMap<Graph.Node, Integer> degree;

    //map from node -> list of moves it is associated with
    HashMap<Graph.Node, HashSet<ASMInstr>> moveList;

    //alias - when MOV(u,v) coalesced, then v is in coalescedNodes and alias(v)=u
    HashMap<Graph.Node, Graph.Node> alias;

    //chosen color for each node
    HashMap<Graph.Node, Integer> color;

    ASMGraph cfg;
    InterferenceGraph interference;
    LiveVariableDFA liveness;
    List<ASMInstr> instrs;

    int K = 16;//num regs I think

    RegAllocationColoringVisitor() {
    }

    public void allocate(List<ASMInstr> instrs) {
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

        livenessAnalysis();
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
            } else if (spillWorklist.size() != 0) {
                selectSpill();
            }
        }
        assignColors();
        if (spilledNodes.size() != 0) {
            List<ASMInstr> new_program = rewriteProgram(new ArrayList<>(spilledNodes));
            allocate(new_program);
        }
    }

    public void livenessAnalysis() {
        //TODO build CFG
    }

    public void build() {
        //TODO initialize interference graph
        //foralll blocks b in program
        for (Graph<ASMInstr>.Node b : cfg.getAllNodes()) {
            //live = liveout(b)
            Map<Graph.Node, Set<ASMExprRegReplaceable>> outs = liveness.getOutMap();
            Set<ASMExprRegReplaceable> live = outs.get(b);
            //forall instructions in b in reverse order (1 instruction in b for us)
            ASMInstr i = b.getT();
            //if ismoveinstr(i)
            if (i.hasNewDef()) {
                //live = live\use(i)
                live = Sets.difference(live, liveness.use(b));
                //forall n in def(i)+use(i)
                for (ASMExprRegReplaceable n : new HashSet<>(Sets.union(liveness.def(b), liveness.use(b)))) {
                    //movelist[n] <- movelist[n] + {I}
                    moveList.get(n).add(i);
                }
                //worklistMoves = workListMoves + {I}
                worklistMoves.add(i);
            }
            //live = live + def(i)
            live = new HashSet<>(Sets.union(live, liveness.def(b)));
            //forall d in def(i)
            for (ASMExprRegReplaceable d : liveness.def(b)) {
                //forall l in live
                for (ASMExprRegReplaceable l : live) {
                    //addEdge(l,d)
                    addEdge(l, d);
                }
            }
            //live = use(i)+ (live\def(i)
            live = new HashSet<>(Sets.union(liveness.use(b), Sets.difference(live, liveness.def(b))));
            //TODO what do we do with live? store it back into liveness outmap?
        }
    }

    public void makeWorkList() {
        //forall n in initial
        for (Graph.Node n : initial) {
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

    public void simplify() {
        //TODO selecting things from worklist
        //let n be in simplifyWorklist
        Graph.Node n = new ArrayList<>(simplifyWorklist).get(0);
        //simplifyWorklist = simplifyWorklist \ {n}
        simplifyWorklist.remove(n);
        //push n to selectStack
        selectStack.push(n);
        for (Graph.Node m : getAdjacent(n)) {
            decrementDegree(m);
        }
    }

    public void coalesce() {
        //TODO
    }

    public void freeze() {
        //TODO
    }

    public void selectSpill() {
        //TODO
    }

    public void assignColors() {
        while (!selectStack.empty()) {
            Graph.Node n = selectStack.pop();
            //okColors = {0,...K-1}
            //TODO right now colors are ints
            Set<Integer> okColors = new HashSet<>();
            for (int i = 0; i < K; i++) {
                okColors.add(i);
            }
            for (Graph.Node w : adjList.get(n)) {
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
        for (Graph.Node n : coalescedNodes) {
            color.put(n, color.get(getAlias(n)));
        }
    }

    public List<ASMInstr> rewriteProgram(List<Graph.Node> spilledNodes) {
        //TODO
        return null;
    }

    //HELPERS DOWN HERE
    public void addEdge(ASMExprRegReplaceable u, ASMExprRegReplaceable v) {
        //if (u,v) not in adjSet and u != v
        Graph.Node uNode = interference.tnode(u);
        Graph.Node vNode = interference.tnode(v);
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

    public Set<Graph.Node> getAdjacent(Graph.Node n) {
        //adjList[n]\(activeMoves + coalescedNodes)
        return new HashSet<>(Sets.difference(
                adjList.get(n),
                Sets.union(new HashSet<>(selectStack), coalescedNodes)
        ));
    }

    public Set<ASMInstr> getNodeMoves(Graph.Node n) {
        //moveList[n] intersection (activeMoves + worklistMoves)
        return new HashSet<>(Sets.intersection(
                moveList.get(n),
                Sets.union(activeMoves, worklistMoves)
        ));
    }

    public boolean isMoveRelated(Graph.Node n) {
        return getNodeMoves(n).size() != 0;
    }

    public void decrementDegree(Graph.Node m) {
        //let d = degree[m]
        int d = degree.get(m);
        //degree[m] = d-1
        degree.put(m, d - 1);
        //if d == K
        if (d == K) {
            //enableMoves({m} + adjacent(m))
            Set<Graph.Node> s = new HashSet<>();
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

    public void enableMoves(Set<Graph.Node> nodes) {
        for (Graph.Node n : nodes) {
            for (ASMInstr m : activeMoves) {
                activeMoves.remove(m);
                worklistMoves.add(m);
            }
        }
    }

    public void addWorkList(Graph.Node u) {
        if (!precolored.contains(u) && !(isMoveRelated(u)) && degree.get(u) < K) {
            freezeWorklist.remove(u);
            simplifyWorklist.add(u);
        }
    }

    public boolean ok(Graph.Node t, Graph.Node r) {
        return degree.get(t) < K && precolored.contains(t) && adjSet.contains(new Pair<>(t, r));
    }

    public boolean conservative(Set<Graph.Node> nodes) {
        int k = 0;
        for (Graph.Node n : nodes) {
            if (degree.get(n) >= K) {
                k = k + 1;
            }
        }
        return k < K;
    }

    public Graph.Node getAlias(Graph.Node n) {
        if (coalescedNodes.contains(n)) {
            return getAlias(alias.get(n));
        }
        return n;
    }
}
