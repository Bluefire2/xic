package kc875.asm.dfa;

import kc875.asm.ASMExprTemp;
import kc875.asm.ASMInstr_2Arg;
import kc875.cfg.Graph;

import java.util.List;

abstract public class InterferenceGraph extends Graph {
    abstract public Node tnode(ASMExprTemp temp);
    //temp->node

    abstract public ASMExprTemp gtemp(Node node);
    //node->temp

    abstract public List<ASMInstr_2Arg> moves();
    //which move instrs are associated with this graph

    public int spillCost(Node n) {
        return 1;//naive spilling
    }
}
