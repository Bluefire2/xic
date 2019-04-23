package edu.cornell.cs.cs4120.xic.ir.visit;

import edu.cornell.cs.cs4120.xic.ir.*;
import edu.cornell.cs.cs4120.xic.ir.dfa.AvailableExprsDFA;
import edu.cornell.cs.cs4120.xic.ir.dfa.IRGraph;
import kc875.cfg.Graph;
import kc875.utils.SetWithInf;

import java.util.*;

public class CommonSubexprElimVisitor {

    public CommonSubexprElimVisitor() { }

    /**
     * Build the per-function control graph.
     * @param irnode The root IR node of the function declaration
     * @return an IRGraph, with basic blocks as nodes and jumps as edges.
     */
    public IRGraph buildCFG(IRFuncDecl irnode) {
        List<IRStmt> stmts = new ArrayList<>();
        IRStmt topstmt = irnode.body();
        if (topstmt instanceof IRSeq) {
            stmts.addAll(((IRSeq) topstmt).stmts());
        }
        else stmts.add(topstmt);

        List<IRStmt> basicBlocks = new ArrayList<>();
        HashMap<String, Integer> nodeLabelMap = new HashMap<>();
        HashMap<Integer, List<String>> jumps = new HashMap<>();

        IRLabel start = new IRLabel(irnode.name());

        IRSeq curr = new IRSeq();

        for (IRStmt s : stmts) {
            if (s instanceof IRLabel) {
                basicBlocks.add(curr);
                curr = new IRSeq();
                curr.stmts().add(s);
                nodeLabelMap.put(((IRLabel)s).name(), basicBlocks.size());
            }
            else curr.stmts().add(s);
            if (s instanceof IRJump ||
                    s instanceof IRCJump ||
                    s instanceof IRReturn) {
                basicBlocks.add(curr);
                curr = new IRSeq();
                List<String> blockjumps = new ArrayList<>();
                if (s instanceof IRCJump) {
                    IRCJump sj = (IRCJump) s;
                    blockjumps.add(sj.trueLabel());
                    blockjumps.add(sj.falseLabel());
                }
                jumps.put(basicBlocks.size(), blockjumps);
            }
        }

        IRGraph irGraph = new IRGraph(basicBlocks);
        //TODO: add edges

        return irGraph;
    }

    public IRCompUnit removeCommonSubExpressions(IRCompUnit irnode) {
        for (IRFuncDecl funcDecl : irnode.functions().values()) {
            Graph irGraph = buildCFG(funcDecl);
            AvailableExprsDFA availableExprsDFA = new AvailableExprsDFA(irGraph);



        }
        //TODO
        return null;
    }

    /**
     * Get the subexpressions of a given IR node
     * @param irNode An IR node with 0 or more children
     * @return A set of all the children of irNode that are of type IRExpr.
     */
    public SetWithInf<IRExpr> getSubExpressions(IRNode irNode) {
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
    public SetWithInf<IRExpr> exprsContainingTemp(IRTemp t, List<IRExpr> exprList) {
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
    public SetWithInf<IRExpr> possibleAliasExprs(IRExpr e, List<IRExpr> exprList) {
        //TODO: how to do this at the IR level??
        return null;
    }

    /**
     * Returns the set of expressions that can be modified by a function call
     * to f.
     * @param f An IR function declaration
     * @param exprList List of expressions to be searched
     * @return The subset of exprlist containing any expression [e[ that could
     * be modified by a call to f.
     */
    public SetWithInf<IRExpr> exprsCanBeModified(IRFuncDecl f, List<IRExpr> exprList) {
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
