package asm.graph;

import asm.*;
import java.util.List;

public class ASMFlowGraph extends FlowGraph {
    public ASMInstr instr(GraphNode n){
        return null;
        //TODO
    }

    public ASMFlowGraph(List<ASMInstr> instrs){
        //TODO
    }

    @Override
    public List<ASMExprTemp> gen(GraphNode n) {
        return null;
    }

    @Override
    public List<ASMExprTemp> kill(GraphNode n) {
        return null;
    }

    @Override
    public boolean isMove(GraphNode n) {
        return false;
    }

    @Override
    public void show() {

    }
}
