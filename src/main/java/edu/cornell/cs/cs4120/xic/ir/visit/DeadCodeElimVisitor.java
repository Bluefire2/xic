package edu.cornell.cs.cs4120.xic.ir.visit;

import edu.cornell.cs.cs4120.xic.ir.*;
import edu.cornell.cs.cs4120.xic.ir.dfa.IRGraph;
import edu.cornell.cs.cs4120.xic.ir.dfa.LivenessDFA;
import kc875.cfg.Graph;
import java.util.ArrayList;
import java.util.List;

public class DeadCodeElimVisitor {

    private IRGraph irGraph;

    public DeadCodeElimVisitor() {}

    /**
     * Removes dead code, defined as any assignment x = e where x is not live
     * out of the node.
     * @param irnode
     * @return irnode with all dead code removed.
     */
    public IRCompUnit removeDeadCode(IRCompUnit irnode) {
        IRCompUnit optimizedCompUnit = new IRCompUnit(irnode.name());
        for (IRFuncDecl funcDecl : irnode.functions().values()) {
            irGraph = IRGraph.buildCFG(funcDecl);
            LivenessDFA livenessDFA = new LivenessDFA(irGraph);
            livenessDFA.runWorklistAlgo();

            IRSeq seq = new IRSeq();

            for (Graph<IRStmt>.Node n : irGraph.getAllNodes()) {
                IRStmt stmt = n.getT();
                List<IRStmt> stmts = new ArrayList<>();
                if (stmt instanceof IRSeq) {
                    stmts.addAll(((IRSeq) stmt).stmts());
                }
                else stmts.add(stmt);

                for (IRStmt s : stmts) {
                    if (s instanceof IRMove) {
                        if (((IRMove) s).target() instanceof IRTemp) {
                            IRTemp tmp = (IRTemp) ((IRMove) s).target();
                            if(!(livenessDFA.getOutMap().get(n).contains(tmp))) {
                                continue;
                            }
                        }
                    }
                    seq.stmts().add(s);
                }

                irGraph.setStmt(n, seq);
            }

        IRFuncDecl optimizedFuncDecl = new IRFuncDecl(funcDecl.name(),
                IRGraph.flattenCFG((IRGraph) irGraph));
        optimizedCompUnit.functions().put(funcDecl.name(), optimizedFuncDecl);
    }
        return optimizedCompUnit;
    }
}
