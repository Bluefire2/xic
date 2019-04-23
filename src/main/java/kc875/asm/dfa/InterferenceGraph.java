package kc875.asm.dfa;

import kc875.asm.ASMExprRegReplaceable;
import kc875.asm.ASMInstr;
import kc875.cfg.Graph;

import java.util.List;

abstract public class InterferenceGraph extends Graph<ASMExprRegReplaceable> {
    abstract public Node tnode(ASMExprRegReplaceable temp);
    //temp->node

    abstract public ASMExprRegReplaceable gtemp(Node node);
    //node->temp

    abstract public List<ASMInstr> moves();
    //which move instrs are associated with this graph

    public int spillCost(Node n) {
        return 1;//naive spilling
    }
}
