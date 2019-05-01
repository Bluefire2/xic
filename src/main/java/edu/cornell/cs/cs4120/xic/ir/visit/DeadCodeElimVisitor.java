package edu.cornell.cs.cs4120.xic.ir.visit;

import edu.cornell.cs.cs4120.xic.ir.*;
import edu.cornell.cs.cs4120.xic.ir.dfa.IRGraph;
import edu.cornell.cs.cs4120.xic.ir.dfa.LivenessDFA;

import java.util.ArrayList;
import java.util.List;

public class DeadCodeElimVisitor {

    private IRGraph irGraph;

    public DeadCodeElimVisitor() { }

    /**
     * Removes dead code, defined as any assignment x = e where x is not live
     * out of the node.
     *
     * @param irnode
     * @return irnode with all dead code removed.
     */
    public IRCompUnit removeDeadCode(IRCompUnit irnode) {
        IRCompUnit optimizedCompUnit = new IRCompUnit(irnode.name());
        for (IRFuncDecl funcDecl : irnode.functions().values()) {
            irGraph = new IRGraph(funcDecl);
            LivenessDFA livenessDFA = new LivenessDFA(irGraph);
            livenessDFA.runWorklistAlgo();

            IRStmt body = funcDecl.body();
            IRSeq stmts = body instanceof IRSeq ? (IRSeq) body : new IRSeq(body);

            List<IRStmt> listStmts = stmts.stmts();

            for (int i = 0; i < listStmts.size(); i++) {
                IRSeq seq = new IRSeq();
                IRStmt s = listStmts.get(i);
                IRGraph.Node n = ((IRGraph) livenessDFA.getGraph()).getNode(s);

                if (s instanceof IRMove) {
                    if (((IRMove) s).target() instanceof IRTemp) {
                        IRTemp tmp = (IRTemp) ((IRMove) s).target();
                        if (!(livenessDFA.getOutMap().get(n).contains(tmp))) {
                            continue;
                        }
                    }
                }
                seq.stmts().add(s);

                listStmts.set(i, seq);
            }

            IRFuncDecl optimizedFuncDecl = new IRFuncDecl(funcDecl.name(),
                    removeNestedIRSeqs(new IRSeq(listStmts)));
            optimizedCompUnit.functions().put(funcDecl.name(), optimizedFuncDecl);
        }
        return optimizedCompUnit;
    }

    private IRSeq removeNestedIRSeqs(IRSeq stmt) {
        List<IRStmt> stmts = new ArrayList<>();
        for (IRStmt s : stmt.stmts()) {
            if (s instanceof IRSeq) {
                stmts.addAll((removeNestedIRSeqs((IRSeq) s)).stmts());
            }
            else stmts.add(s);
        }
        return new IRSeq(stmts);
    }
}
