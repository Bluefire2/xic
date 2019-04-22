package kc875.asm.graph;

import kc875.asm.ASMExprRegReplaceable;
import kc875.asm.ASMExprTemp;
import kc875.asm.ASMInstr_2Arg;
import kc875.cfg.GraphNode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Liveness extends InterferenceGraph {
    //class for liveness analysis (used in reg allocation)

    //what is live at in/out of each graph node
    private HashMap<GraphNode, List<ASMExprRegReplaceable>> inLiveMap;
    private HashMap<GraphNode, List<ASMExprRegReplaceable>> outLiveMap;

    public Liveness(FlowGraph flow){
        this.inLiveMap = new HashMap<>();
        this.outLiveMap = new HashMap<>();
        for (GraphNode n : flow.getNodes()){
            inLiveMap.put(n, new ArrayList<>());
            outLiveMap.put(n, new ArrayList<>());
        }
    }

    //TODO everything
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
