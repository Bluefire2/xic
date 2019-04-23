package edu.cornell.cs.cs4120.xic.ir.visit;

import edu.cornell.cs.cs4120.xic.ir.*;
import edu.cornell.cs.cs4120.xic.ir.dfa.AvailableExprsDFA;
import edu.cornell.cs.cs4120.xic.ir.dfa.IRGraph;
import kc875.cfg.Graph;
import kc875.utils.SetWithInf;
import java.util.HashSet;
import java.util.List;

public class CommonSubexprElimVisitor {

    public CommonSubexprElimVisitor() { }

    public List<IRStmt> getBasicBlocks(IRCompUnit irCompUnit) {
        //TODO

        //if function decl - new block
        //within function decl: if jump, cjump, ret, instr before label -> end block
        //if call, jump, ret -> create edge
        return null;
    }

    public IRCompUnit removeCommonSubExpressions(IRCompUnit irnode) {
        Graph irGraph = new IRGraph(getBasicBlocks(irnode));
        AvailableExprsDFA availableExprsDFA = new AvailableExprsDFA(irGraph);


        //TODO
        return null;

    }

    /**
     * Return the subset of a list of IR expressions that reference a given temp.
     * @param t IR level temporary variable
     * @param exprlst the list of IR expressions to be searched
     * @return the subset of exprlst containing references to t
     */
    public SetWithInf<IRExpr> exprsContainingTemp(IRTemp t, List<IRExpr> exprlst) {
        ListChildrenVisitor lcv = new ListChildrenVisitor();
        HashSet<IRExpr> exprSet = new HashSet<>();

        for (IRExpr expr : exprlst) {
            List<IRNode> children = lcv.visit(expr);
            for (IRNode n : children) {
                if (n instanceof IRTemp) {
                    IRTemp tn = (IRTemp) n;
                    if (tn.name().equals(t.name())) exprSet.add(expr);
                }
            }
        }

        return new SetWithInf<>(exprSet);
    }

    /**
     * Returns the set of expressions used in a mem that may be an alias for e.
     * Two memory operands are considered aliases unless:
     *  1. One is a stack location and the other is a heap location
     *  2. The operands are of format [rbp + i] and [rbp + j], and i =/= j
     *  3. The operand points to immutable memory
     *  4. The types of the operands are incompatible
     * @param e IR expression used in a mem: [e]
     * @param exprList the list of IR expression to be searched
     * @return the subset of exprlst containing any expression [e'] that may be
     * an alias for [e]
     */
    public SetWithInf<IRExpr> possibleAliasExprs(IRExpr e, List<IRExpr> exprList) {



        return null;
    }




}
