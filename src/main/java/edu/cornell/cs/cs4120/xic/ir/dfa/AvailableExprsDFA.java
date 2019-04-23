package edu.cornell.cs.cs4120.xic.ir.dfa;

import edu.cornell.cs.cs4120.xic.ir.IRExpr;
import kc875.cfg.DFAFramework;
import kc875.cfg.Graph;
import kc875.utils.SetWithInf;

public class AvailableExprsDFA extends DFAFramework<SetWithInf<IRExpr>> {

    public AvailableExprsDFA(Graph graph) {
        super(
                graph,
                Direction.FORWARD,
                (node, l) -> l.union(exprs(node).diff(kill(node))),
                SetWithInf::intersect,
                SetWithInf.infSet()
        );
    }

    public static SetWithInf<IRExpr> exprs (Graph.Node gn) {
        //TODO
        return new SetWithInf<>();
    }

    public static SetWithInf<IRExpr> kill (Graph.Node gn) {
        //TODO
        return new SetWithInf<>();
    }

}
