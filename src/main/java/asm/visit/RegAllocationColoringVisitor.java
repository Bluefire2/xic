package asm.visit;

import java.util.HashSet;

import asm.ASMInstr_2Arg;
import asm.graph.*;
import polyglot.util.Pair;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;

public class RegAllocationColoringVisitor {
    //every node is in exactly one of these sets
    HashSet<GraphNode> precolored;
    HashSet<GraphNode> initial;
    HashSet<GraphNode> simplifyWorklist;
    HashSet<GraphNode> freezeWorklist;
    HashSet<GraphNode> spillWorklist;
    HashSet<GraphNode> spilledNodes;
    HashSet<GraphNode> coalescedNodes;
    HashSet<GraphNode> coloredNodes;
    HashSet<GraphNode> selectStack;

    //every move is in exactly one of these sets
    HashSet<ASMInstr_2Arg> coalescedMoves;
    HashSet<ASMInstr_2Arg> constrainedMoves;
    HashSet<ASMInstr_2Arg> frozenMoves;
    HashSet<ASMInstr_2Arg> worklistMoves;
    HashSet<ASMInstr_2Arg> activeMoves;

    //set of interference edges - reverse edges must always be in the set
    HashSet<Pair<GraphNode, GraphNode>> adjSet;

    //list repr of the graph, node -> list of interfering nodes
    HashMap<GraphNode, List<GraphNode>> adjList;

    //map from node -> list of moves it is associated with
    HashMap<GraphNode, List<ASMInstr_2Arg>> moveList;

    //alias - when MOV(u,v) coalesced, then v is in coalescedNodes and alias(v)=u
    HashMap<GraphNode, GraphNode> alias;

    //chosen color for each node
    HashMap<GraphNode, String> color;

    RegAllocationColoringVisitor(){
        //TODO
    }

    public void allocate(){
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
            rewriteProgram(new ArrayList<GraphNode>(spilledNodes));
            allocate();
        }
    }

    public void livenessAnalysis(){
        //TODO see Liveness class
    }

    public void build(){
        //TODO
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

    public void rewriteProgram(List<GraphNode> spilledNodes){
        //TODO
    }

}
