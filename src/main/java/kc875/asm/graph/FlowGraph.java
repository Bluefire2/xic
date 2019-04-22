package kc875.asm.graph;

import kc875.asm.ASMExprRegReplaceable;
import kc875.cfg.Graph;
import kc875.cfg.GraphNode;

import java.util.List;

public abstract class FlowGraph extends Graph {
    public abstract List<ASMExprRegReplaceable> gen(GraphNode n);
    public abstract List<ASMExprRegReplaceable> kill(GraphNode n);
    public abstract boolean isMove(GraphNode n);
    public abstract void show();
}