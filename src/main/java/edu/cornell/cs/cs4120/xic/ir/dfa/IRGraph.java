package edu.cornell.cs.cs4120.xic.ir.dfa;

import edu.cornell.cs.cs4120.xic.ir.IRExpr;
import edu.cornell.cs.cs4120.xic.ir.IRStmt;
import kc875.cfg.Graph;

import java.util.HashMap;
import java.util.List;

public class IRGraph extends Graph<IRStmt> {

    private HashMap<Node, List<IRExpr>> genMap;
    private HashMap<Node, List<IRExpr>> killMap;

    public IRGraph(List<IRStmt> quadruples){
        //TODO: complete, change from quadruple to IRStmt
        for (IRStmt q : quadruples) {
            Graph.Node n = new Graph.Node(q);
            addOtherNode(n);
            genMap.put(n, getGen(n));
            killMap.put(n, getKill(n));
        }
    }

    public List<IRExpr> getGen(Node n) {
        return genMap.get(n);
    }

    public List<IRExpr> getKill(Node n) {
        return killMap.get(n);
    }
}
