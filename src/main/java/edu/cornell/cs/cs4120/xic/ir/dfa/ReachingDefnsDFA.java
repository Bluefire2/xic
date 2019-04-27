package edu.cornell.cs.cs4120.xic.ir.dfa;

import com.google.common.collect.Sets;
import edu.cornell.cs.cs4120.xic.ir.IRExpr;
import edu.cornell.cs.cs4120.xic.ir.IRMove;
import edu.cornell.cs.cs4120.xic.ir.IRStmt;
import edu.cornell.cs.cs4120.xic.ir.IRTemp;
import kc875.cfg.DFAFramework;

import java.util.HashSet;
import java.util.Set;

public class ReachingDefnsDFA extends DFAFramework<Set<IRGraph.Node>, IRStmt> {

    public ReachingDefnsDFA(IRGraph graph) {
        super(
                graph,
                DFAFramework.Direction.FORWARD,
                (node, l) -> new HashSet<>(Sets.union(gen(node),
                        Sets.difference(l, kill(node, graph)))),
                HashSet::new,
                (l1, l2) -> new HashSet<IRGraph.Node>(Sets.union(l1, l2)),
                new HashSet<>()
        );
    }

    public static Set<IRGraph.Node> gen(IRGraph.Node node) {
        HashSet<IRGraph.Node> genSet = new HashSet<>();
        IRStmt s = node.getT();
        if (s instanceof IRMove) {
            if (((IRMove) s).target() instanceof IRTemp) genSet.add(node);
        }
        return genSet;
    }

    public static Set<IRGraph.Node> kill(IRGraph.Node node, IRGraph graph) {
        IRStmt s = node.getT();
        if (s instanceof IRMove) {
            IRExpr target = ((IRMove) s).target();
            if (target instanceof IRTemp) return defs((IRTemp) target, graph);
        }
        return new HashSet<>();
    }

    /**
     * Return the set of all nodes that define a given variable.
     * @param x
     * @param graph
     * @return Set of nodes in graph that define x.
     */
    public static Set<IRGraph.Node> defs(IRTemp x, IRGraph graph) {
        Set<IRGraph.Node> defSet = new HashSet<>();
        for (IRGraph.Node n : graph.getAllNodes()) {
            IRStmt s = n.getT();
            if (s instanceof IRMove) {
                IRExpr target = ((IRMove) s).target();
                if (target instanceof IRTemp &&
                        ((IRTemp) target).name().equals(x.name())) {
                    defSet.add(n);
                }
            }
        }
        return defSet;
    }

}
