package asm.graph;
import asm.*;
import java.util.List;

public abstract class FlowGraph extends Graph {
    public abstract List<ASMExprTemp> gen(GraphNode n);
    public abstract List<ASMExprTemp> kill(GraphNode n);
    public abstract boolean isMove(GraphNode n);
    public abstract void show();
}
