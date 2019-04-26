package edu.cornell.cs.cs4120.xic.ir.dfa;

import com.google.common.collect.Sets;
import edu.cornell.cs.cs4120.xic.ir.IRStmt;
import edu.cornell.cs.cs4120.xic.ir.IRTemp;
import kc875.cfg.DFAFramework;
import java.util.HashSet;
import java.util.Set;

public class ReachingDefnsDFA extends DFAFramework<Set<IRTemp>, IRStmt> {

    public ReachingDefnsDFA(IRGraph graph) {
        super(
                graph,
                DFAFramework.Direction.FORWARD,
                (node, l) -> new HashSet<>(Sets.union(gen(node),
                        Sets.difference(l, kill(node)))),
                HashSet::new,
                (l1, l2) -> new HashSet<>(Sets.union(l1, l2)),
                new HashSet<>()
        );
    }

    public static Set<IRTemp> gen(IRGraph.Node node) {
        //TODO
        return new HashSet<>();

    }

    public static Set<IRTemp> kill(IRGraph.Node node) {
        //TODO
        return new HashSet<>();
    }

    public static Set<IRGraph.Node> defs(IRTemp tmp) {
        //TODO
        return new HashSet<>();
    }

}
