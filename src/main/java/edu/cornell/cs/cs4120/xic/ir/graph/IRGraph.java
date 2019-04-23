package edu.cornell.cs.cs4120.xic.ir.graph;

import edu.cornell.cs.cs4120.xic.ir.IRExpr;
import edu.cornell.cs.cs4120.xic.ir.IRStmt;
import kc875.cfg.Graph;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class IRGraph extends Graph<IRStmt> {

    // TODO: remove nodeMap. The graph nodes already wrap IR nodes.
    private HashMap<Node, Quadruple> nodeMap;
    private HashMap<Node, List<IRExpr>> genMap;
    private HashMap<Node, List<IRExpr>> killMap;

    public IRGraph(List<Quadruple> quadruples){
        //TODO: complete, change from quadruple to IRStmt
        for (Quadruple q : quadruples) {
            Node n = new Node();
            nodeMap.put(n, q);
            genMap.put(n, getGen(n));
            killMap.put(n, getKill(n));
        }
    }

    public Quadruple quadruple(Node n){
        return nodeMap.get(n);
    }

    public List<IRExpr> getAvailableExpressions(Quadruple q) {
        //TODO
        return null;
    }

    public List<IRExpr> getGen(Node n) {
        return genMap.get(n);
    }

    public List<IRExpr> getKill(Node n) {
        return killMap.get(n);
    }

    public Set<IRExpr> getIn(Node n) {
        //TODO
        return null;
    }

    public List<IRExpr> getOut(Node n) {
        //TODO
        return null;
    }

    @Override
    public void show() {
        //TODO
    }
}
