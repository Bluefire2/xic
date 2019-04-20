package asm.graph;
import asm.*;
import java.util.List;

abstract public class InterferenceGraph extends Graph {
    abstract public GraphNode tnode(ASMExprTemp temp);
    //temp->node

    abstract public ASMExprTemp gtemp(GraphNode node);
    //node->temp

    abstract public List<ASMInstr_2Arg> moves();
    //which move instrs are associated with this graph

    abstract public int spillCost(GraphNode n);
    //estimate extra instructions if n were kept in memory instead of regs
}
