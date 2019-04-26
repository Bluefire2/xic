package edu.cornell.cs.cs4120.xic.ir.dfa;

import com.google.common.collect.Sets;
import edu.cornell.cs.cs4120.xic.ir.IRStmt;
import edu.cornell.cs.cs4120.xic.ir.IRTemp;
import kc875.cfg.DFAFramework;
import java.util.HashSet;
import java.util.Set;

public class LivenessDFA extends DFAFramework<Set<IRTemp>, IRStmt> {

    public LivenessDFA(IRGraph graph) {
        super(
                graph,
                Direction.BACKWARD,
                (node, l) -> new HashSet<>(Sets.union(use(node),
                        Sets.difference(l, def(node)))),
                HashSet::new,
                (l1, l2) -> new HashSet<>(Sets.union(l1, l2)),
                new HashSet<>()
        );
    }

    public static Set<IRTemp> def(IRGraph.Node node) {
        //TODO
        return new HashSet<>();

    }

    public static Set<IRTemp> use(IRGraph.Node node) {
        //TODO
        return new HashSet<>();
    }

}
