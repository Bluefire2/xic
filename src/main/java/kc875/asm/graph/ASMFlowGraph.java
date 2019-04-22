package kc875.asm.graph;

import kc875.asm.ASMExprRegReplaceable;
import kc875.asm.ASMInstr;
import kc875.asm.ASMOpCode;
import kc875.cfg.GraphNode;

import java.util.HashMap;
import java.util.List;

public class ASMFlowGraph extends FlowGraph {
    private HashMap<GraphNode, ASMInstr> instrMap;
    private HashMap<GraphNode, List<ASMExprRegReplaceable>> genMap;
    private HashMap<GraphNode, List<ASMExprRegReplaceable>> killMap;

    public List<ASMExprRegReplaceable> getUse(ASMInstr i) {
        //TODO
        return null;
    }

    public List<ASMExprRegReplaceable> getDef(ASMInstr i) {
        //TODO
        return null;
    }

    //constructor that returns a new flow graph
    public ASMFlowGraph(List<ASMInstr> instrs){
        for (ASMInstr i : instrs){
            GraphNode n = newNode();
            instrMap.put(n, i);
            genMap.put(n, getUse(i));
            killMap.put(n, getDef(i));
            //TODO configure edges using inherited graph methods
        }
    }

    public ASMInstr instr(GraphNode n){
        return instrMap.get(n);
    }

    //USE - what temps are used at this node!
    public List<ASMExprRegReplaceable> gen(GraphNode n) {
        return genMap.get(n);
    }

    //DEF - what temps are def'd at this node!
    public List<ASMExprRegReplaceable> kill(GraphNode n) {
        return killMap.get(n);
    }

    public List<ASMExprRegReplaceable> use(GraphNode n) {
        return gen(n);
    }

    public List<ASMExprRegReplaceable> def(GraphNode n) {
        return kill(n);
    }

    @Override
    public boolean isMove(GraphNode n) {
        return instrMap.get(n).getOpCode() == ASMOpCode.MOV;
    }

    @Override
    public void show() {
        //TODO
    }
}
