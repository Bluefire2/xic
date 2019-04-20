package asm.graph;

import asm.ASMExprReg;
import asm.ASMExprRegReplaceable;
import asm.ASMExprTemp;
import asm.ASMInstr_2Arg;

import java.util.HashMap;
import java.util.List;
import java.util.Dictionary;
import java.util.ArrayList;

public class Liveness extends InterferenceGraph {
    //class for liveness analysis (used in reg allocation)
    
    //what is live at in/out of each graph node
    private HashMap<GraphNode, List<ASMExprRegReplaceable>> inLiveMap;
    private HashMap<GraphNode, List<ASMExprRegReplaceable>> outLiveMap;

    //TODO everything
    public Liveness(FlowGraph flow){
        this.inliveMap = new HashMap<>();
        this.outliveMap = new HashMap<>();
        for (GraphNode n : flow.getNodes()){
            liveMap.put(n, new ArrayList<>());
        }
    }

    @Override
    public GraphNode tnode(ASMExprTemp temp) {
        return null;
    }

    @Override
    public ASMExprTemp gtemp(GraphNode node) {
        return null;
    }

    @Override
    public List<ASMInstr_2Arg> moves() {
        return null;
    }
}
