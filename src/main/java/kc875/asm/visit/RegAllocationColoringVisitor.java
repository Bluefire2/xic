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
    HashSet<Graph.Node> selectStack; //stacks w/ temps removed from graph

    //every move is in exactly one of these sets
    HashSet<ASMInstr> coalescedMoves; //coalesced moves
    HashSet<ASMInstr> constrainedMoves; //moves whose src/target interfere
    HashSet<ASMInstr> frozenMoves; //moves no longer considered for coalescing
    HashSet<ASMInstr> worklistMoves; //moves enabled for possible coalescing
    HashSet<ASMInstr> activeMoves; //moves not ready for coalescing

    //set of interference edges - reverse edges must always be in the set
    HashSet<Pair<Graph.Node, Graph.Node>> adjSet;

    //list repr of the graph, node -> list of interfering nodes
    HashMap<Graph.Node, List<Graph.Node>> adjList;

    //map from node -> list of moves it is associated with
    HashMap<Graph.Node, HashSet<ASMInstr>> moveList;

    //alias - when MOV(u,v) coalesced, then v is in coalescedNodes and alias(v)=u
    HashMap<Graph.Node, Graph.Node> alias;

    //chosen color for each node
    HashMap<Graph.Node, String> color;

    ASMGraph cfg;
    InterferenceGraph interference;
    LiveVariableDFA liveness;
    List<ASMInstr> instrs;

    RegAllocationColoringVisitor(){
    }

    public void allocate(List<ASMInstr> instrs){
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
        selectStack = new HashSet<>();

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
            if (simplifyWorklist.size() != 0){
                simplify();
            } else if (worklistMoves.size() != 0){
                coalesce();
            } else if (freezeWorklist.size() != 0){
                freeze();
            } else if (spillWorklist.size() != 0){
                selectSpill();
            }
        }
        assignColors();
        if (spilledNodes.size() != 0){
            List<ASMInstr> new_program = rewriteProgram(new ArrayList<>(spilledNodes));
            allocate(new_program);
        }
    }

    public void livenessAnalysis(){
        //TODO build CFG
    }

    public void build(){
        for (Graph<ASMInstr>.Node b : cfg.getAllNodes()){
            Map<Graph.Node, Set<ASMExprRegReplaceable>> outs = liveness.getOutMap();
            Set<ASMExprRegReplaceable> live = outs.get(b);
            ASMInstr i = b.getT();
            if (i.isDestChanged()){
                live = Sets.difference(live, liveness.use(b));
                for (ASMExprRegReplaceable n : Sets.union(liveness.def(b), liveness.use(b)).immutableCopy()){
                    moveList.get(n).add(i); //movelist[n] <- movelist[n] + {I}
                }
                worklistMoves.add(i);
            }
            live = Sets.union(live, liveness.def(b)).immutableCopy();
            for (ASMExprRegReplaceable d : liveness.def(b)){
                for (ASMExprRegReplaceable l : live) {
                    //interference.addEdge(interference.tnode(l), interference.tnode(d));
                    //TODO
                }
            }
            live = Sets.union(liveness.use(b), Sets.difference(live, liveness.def(b)).immutableCopy()).immutableCopy();
        }
    }

    public void makeWorkList(){
        //TODO
    }

    public void simplify(){
        //TODO
    }

    public void coalesce(){
        //TODO
    }

    public void freeze(){
        //TODO
    }

    public void selectSpill(){
        //TODO
    }

    public void assignColors(){
        //TODO
    }

    public List<ASMInstr> rewriteProgram(List<Graph.Node> spilledNodes){
        //TODO
        return null;
    }

}
