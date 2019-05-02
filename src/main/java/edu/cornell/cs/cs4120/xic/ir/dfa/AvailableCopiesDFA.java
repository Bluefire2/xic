package edu.cornell.cs.cs4120.xic.ir.dfa;

import com.google.common.collect.Sets;
import edu.cornell.cs.cs4120.xic.ir.IRMove;
import edu.cornell.cs.cs4120.xic.ir.IRStmt;
import edu.cornell.cs.cs4120.xic.ir.IRTemp;
import kc875.cfg.DFAFramework;
import kc875.cfg.Graph;
import kc875.utils.SetWithInf;
import polyglot.util.Pair;

public class AvailableCopiesDFA extends
        DFAFramework<SetWithInf<Pair<IRTemp, IRTemp>>, IRStmt> {

    public AvailableCopiesDFA(IRGraph irGraph) {
        super(
                irGraph,
                Direction.FORWARD,
                // lDiffKill is not inf (postcondition), so the
                // precondition of union is met
                (node, l) -> gen(node).union(lDiffKill(l, node)),
                SetWithInf::infSet,// meet acc
                (l1, l2) -> {
                    if (l1.isInf()) // l1 is top
                        return l2;
                    if (l2.isInf()) // l2 is top
                        return l1;
                    // l1 and l2 are not top, take the normal intersection
                    return new SetWithInf<>(Sets.intersection(
                            l1.getSet(), l2.getSet()
                    ));
                },
                SetWithInf.infSet()// top
        );
    }

    private static SetWithInf<Pair<IRTemp, IRTemp>> gen(
            Graph<IRStmt>.Node node
    ) {
        IRStmt stmt = node.getT();
        if (stmt instanceof IRMove) {
            IRMove m = (IRMove) stmt;
            if (m.target() instanceof IRTemp && m.source() instanceof IRTemp) {
                // x = y; gen (x, y)
                SetWithInf<Pair<IRTemp, IRTemp>> s = new SetWithInf<>();
                s.add(new Pair<>(
                        (IRTemp) m.target(),
                        (IRTemp) m.source()
                ));
                return s;
            }
        }
        return new SetWithInf<>();// return empty set otherwise
    }

    /**
     * Returns the result of l.diff(kill(node)) for this analysis.
     * Postconditions:
     * - The returned set is not infinite.
     */
    private static SetWithInf<Pair<IRTemp, IRTemp>> lDiffKill(
            SetWithInf<Pair<IRTemp, IRTemp>> l,
            Graph<IRStmt>.Node node
    ) {
        IRStmt stmt = node.getT();
        if (l.isInf())// l is top
            // Note: in this analysis, the meet function is intersection and
            // the start node kills everything. So all nodes except the start
            // node effectively have their tops initialized to empty set
            // Ask Anmol if more explanation needed
            return new SetWithInf<>();// kill everything

        if (stmt instanceof IRMove) {
            IRMove m = (IRMove) stmt;
            if (m.target() instanceof IRTemp) {
                // x = e; kill (x, z), (z, x) for any z
                IRTemp x = (IRTemp) m.target();
                // Remove all elements from l with l.part1() = x
                l.removeIf(p -> p.part1().equals(x));
                // Remove all elements from l with l.part2() = x
                l.removeIf(p -> p.part2().equals(x));
                return l;
            }
        }
        return l;// kill nothing
    }

}
