package kc875.asm.dfa;

import kc875.asm.ASMExprRegReplaceable;
import kc875.cfg.Graph;

import java.util.List;

public abstract class FlowGraph extends Graph {
    public abstract List<ASMExprRegReplaceable> gen(kc875.cfg.Graph.Node n);
    public abstract List<ASMExprRegReplaceable> kill(kc875.cfg.Graph.Node n);
    public abstract boolean isMove(kc875.cfg.Graph.Node n);
    public abstract void show();
}
