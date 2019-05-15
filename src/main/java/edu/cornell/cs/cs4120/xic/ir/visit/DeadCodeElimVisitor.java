package edu.cornell.cs.cs4120.xic.ir.visit;

import edu.cornell.cs.cs4120.xic.ir.*;
import edu.cornell.cs.cs4120.xic.ir.dfa.IRGraph;
import edu.cornell.cs.cs4120.xic.ir.dfa.LivenessDFA;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class DeadCodeElimVisitor {

    public DeadCodeElimVisitor() { }

    /**
     * Removes dead code, defined as any assignment x = e where x is not live
     * out of the node.
     *
     * @param ir comp unit of the ir
     * @return ir with all dead code removed.
     */
    public IRCompUnit run(IRCompUnit ir) {
        IRCompUnit optimCompUnit = new IRCompUnit(ir.name());
        for (IRFuncDecl f : ir.functions().values()) {
            IRFuncDecl optimF = removeDeadCode(f);
            optimCompUnit.functions().put(optimF.name(), optimF);
        }
        return optimCompUnit;
    }

    private IRFuncDecl removeDeadCode(IRFuncDecl func) {
        IRGraph graph = new IRGraph(func);
        LivenessDFA dfa = new LivenessDFA(graph);
        dfa.runWorklistAlgo();

        Map<IRGraph.Node, Set<IRTemp>> nodeToLiveVars = dfa.getOutMap();

        IRStmt body = func.body();
        IRSeq stmts = body instanceof IRSeq ? (IRSeq) body : new IRSeq(body);
        List<IRStmt> optimStmts = new ArrayList<>();

        for (int i = 0; i < stmts.stmts().size(); ++i) {
            IRGraph.Node n = graph.getNode(i);

            IRStmt s = stmts.stmts().get(i);
            if (s instanceof IRMove) {
                if (((IRMove) s).target() instanceof IRTemp) {
                    IRTemp target = (IRTemp) ((IRMove) s).target();
                    if (nodeToLiveVars.get(n).contains(target)) {
                        // x in mov x, e is live out, add to optimStmts
                        optimStmts.add(s);
                    } else if (((IRMove) s).source() instanceof IRCall) {
                        // x in mov x, f is not live out, but we don't want
                        // to lose this call to f
                        optimStmts.add(s);
                    }
                    // else skip s
                    continue;
                }
            }
            optimStmts.add(s);
        }

        return new IRFuncDecl(
                func.name(), func.getNumParams(), func.getNumRets(),
                new IRSeq(optimStmts)
        );
    }
}
