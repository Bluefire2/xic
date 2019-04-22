package edu.cornell.cs.cs4120.xic.ir.graph;

import edu.cornell.cs.cs4120.xic.ir.IRExpr;
import kc875.cfg.Graph;
import kc875.cfg.GraphNode;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class IRFlowGraph extends Graph {

    private HashMap<GraphNode, Quadruple> nodeMap;
    private HashMap<GraphNode, List<IRExpr>> genMap;
    private HashMap<GraphNode, List<IRExpr>> killMap;

    public IRFlowGraph(List<Quadruple> quadruples){
        //TODO: complete
        for (Quadruple q : quadruples){
            GraphNode n = new GraphNode(this);
            nodeMap.put(n, q);
            genMap.put(n, getGen(n));
            killMap.put(n, getKill(n));
        }
    }

    public Quadruple quadruple(GraphNode n){
        return nodeMap.get(n);
    }

    public List<IRExpr> getAvailableExpressions(Quadruple q) {
        //TODO
        return null;
    }

    public List<IRExpr> getGen(GraphNode n) {
        return genMap.get(n);
    }

    public List<IRExpr> getKill(GraphNode n) {
        return killMap.get(n);
    }

    public Set<IRExpr> getIn(GraphNode n) {
        //TODO
        return null;
    }

    public List<IRExpr> getOut(GraphNode n) {
        //TODO
        return null;
    }

    @Override
    public void show() {
        //TODO
    }
}
