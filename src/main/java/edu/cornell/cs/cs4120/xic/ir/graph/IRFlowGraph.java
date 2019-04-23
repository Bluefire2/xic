package edu.cornell.cs.cs4120.xic.ir.graph;

import edu.cornell.cs.cs4120.xic.ir.IRExpr;
import kc875.cfg.Graph;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class IRFlowGraph extends Graph {

    private HashMap<kc875.cfg.Node, Quadruple> nodeMap;
    private HashMap<kc875.cfg.Node, List<IRExpr>> genMap;
    private HashMap<kc875.cfg.Node, List<IRExpr>> killMap;

    public IRFlowGraph(List<Quadruple> quadruples){
        //TODO: complete
        for (Quadruple q : quadruples){
            kc875.cfg.Node n = new kc875.cfg.Node(this);
            nodeMap.put(n, q);
            genMap.put(n, getGen(n));
            killMap.put(n, getKill(n));
        }
    }

    public Quadruple quadruple(kc875.cfg.Node n){
        return nodeMap.get(n);
    }

    public List<IRExpr> getAvailableExpressions(Quadruple q) {
        //TODO
        return null;
    }

    public List<IRExpr> getGen(kc875.cfg.Node n) {
        return genMap.get(n);
    }

    public List<IRExpr> getKill(kc875.cfg.Node n) {
        return killMap.get(n);
    }

    public Set<IRExpr> getIn(kc875.cfg.Node n) {
        //TODO
        return null;
    }

    public List<IRExpr> getOut(kc875.cfg.Node n) {
        //TODO
        return null;
    }

    @Override
    public void show() {
        //TODO
    }
}
