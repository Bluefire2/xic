package edu.cornell.cs.cs4120.xic.ir.dfa;

import com.google.common.collect.Lists;
import edu.cornell.cs.cs4120.xic.ir.*;
import edu.cornell.cs.cs4120.xic.ir.visit.ListChildrenVisitor;
import kc875.cfg.DFAFramework;
import kc875.utils.SetWithInf;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class AvailableExprsDFA extends DFAFramework<SetWithInf<IRExpr>, IRStmt> {

    public AvailableExprsDFA(IRGraph graph) {
        super(
                graph,
                Direction.FORWARD,
                (node, l) -> l.union(exprs(node).diff(kill(node))),
                SetWithInf::new,
                SetWithInf::intersect,
                SetWithInf.infSet()
        );
    }

    public static SetWithInf<IRExpr> exprs (IRGraph.Node gn) {
        IRStmt stmt = gn.getT();
        return getSubExpressions(stmt);
    }

    public static SetWithInf<IRExpr> kill (IRGraph.Node gn) {
        IRStmt stmt = gn.getT();
        List<IRStmt> stmts = new ArrayList<>();
        if (stmt instanceof IRSeq) {
            stmts.addAll(((IRSeq) stmt).stmts());
        }
        else stmts.add(stmt);

        SetWithInf sourceKillSet = new SetWithInf();
        SetWithInf destKillSet = new SetWithInf();

        for (IRStmt s : stmts) {
            List<IRExpr> subexpressions = Lists.newArrayList(getSubExpressions(s));
            if (s instanceof IRMove) {
                IRMove smove = (IRMove) s;

                if (smove.target() instanceof IRTemp) {
                    sourceKillSet.union(
                            exprsContainingTemp(
                                    (IRTemp) smove.target(),
                                    subexpressions
                            ));
                }
                else if (smove.target() instanceof IRMem) {
                    sourceKillSet.union(
                            possibleAliasExprs(
                                    ((IRMem) smove.target()).expr(),
                                    subexpressions
                            ));
                }

                if (smove.source() instanceof IRCall) {
                    destKillSet.union(
                            exprsCanBeModified(
                                    ((IRName) ((IRCall) smove.source()).target()).name(),
                                    subexpressions
                            ));
                }
            }
            else if (s instanceof IRCJump) {
                IRCJump jmp = (IRCJump) s;
                if (jmp.cond() instanceof IRCall) {
                    destKillSet.union(
                            exprsCanBeModified(
                                    ((IRName) ((IRCall) jmp.cond()).target()).name(),
                                    subexpressions
                            ));
                }
            }
        }

        return sourceKillSet.union(destKillSet);
    }

    /**
     * Get the subexpressions of a given IR node
     * @param irNode An IR node with 0 or more children
     * @return A set of all the children of irNode that are of type IRExpr.
     */
    public static SetWithInf<IRExpr> getSubExpressions(IRNode irNode) {
        ListChildrenVisitor lcv = new ListChildrenVisitor();
        HashSet<IRExpr> exprSet = new HashSet<>();

        for (IRNode n : lcv.visit(irNode)) {
            if (n instanceof IRExpr) {
                exprSet.add((IRExpr) n);
            }
        }

        return new SetWithInf<>(exprSet);
    }

    /**
     * Return the subset of a list of IR expressions that reference a given temp.
     * @param t IR level temporary variable
     * @param exprList the list of IR expressions to be searched
     * @return the subset of exprlst containing references to t
     */
    public static SetWithInf<IRExpr> exprsContainingTemp(IRTemp t, List<IRExpr> exprList) {
        ListChildrenVisitor lcv = new ListChildrenVisitor();
        HashSet<IRExpr> exprSet = new HashSet<>();

        for (IRExpr expr : exprList) {
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
    public static SetWithInf<IRExpr> possibleAliasExprs(IRExpr e, List<IRExpr> exprList) {
        //TODO: how to do this at the IR level??
        return null;
    }

    /**
     * Returns the set of expressions that can be modified by a function call
     * to f.
     * @param fname An IR function declaration
     * @param exprList List of expressions to be searched
     * @return The subset of exprlist containing any expression [e[ that could
     * be modified by a call to f.
     */
    public static SetWithInf<IRExpr> exprsCanBeModified(String fname, List<IRExpr> exprList) {
        //TODO: use mem aliasing
        ListChildrenVisitor lcv = new ListChildrenVisitor();
        HashSet<IRExpr> exprSet = new HashSet<>();

        for (IRExpr expr : exprList) {
            List<IRNode> children = lcv.visit(expr);
            for (IRNode n : children) {
                if (n instanceof IRMem) {
                    exprSet.add(expr);
                }
            }
        }

        return new SetWithInf<>(exprSet);
    }

}
