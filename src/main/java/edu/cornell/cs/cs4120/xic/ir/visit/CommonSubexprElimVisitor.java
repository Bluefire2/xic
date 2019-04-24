package edu.cornell.cs.cs4120.xic.ir.visit;

import edu.cornell.cs.cs4120.xic.ir.*;
import edu.cornell.cs.cs4120.xic.ir.dfa.AvailableExprsDFA;
import edu.cornell.cs.cs4120.xic.ir.dfa.IRGraph;
import kc875.cfg.Graph;

import java.util.*;

public class CommonSubexprElimVisitor {

    public CommonSubexprElimVisitor() { }

    private int tempcounter;
    public String newTemp() {
        return String.format("cse_t%d", tempcounter++);
    }

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

        return new IRGraph(stmts);
    }

    /**
     * Flatten control flow graph back into IR
     * @param irGraph
     * @return an IR statement (IRSeq) constructed from irGraph
     */
    public IRStmt flattenCFG(IRGraph irGraph) {
        IRSeq retseq = new IRSeq();
        for (Graph.Node n : irGraph.getAllNodes()) {
            IRStmt s = irGraph.getStmt(n);
            if (s instanceof IRSeq) {
                retseq.stmts().addAll(((IRSeq) s).stmts());
            }
            else retseq.stmts().add(s);
        }
        return retseq;
    }


    /**
     * Perform common subexpression elimination
     * @param irnode
     * @return irnode with common subexpressions hoisted and replaced by temps.
     */
    public IRCompUnit removeCommonSubExpressions(IRCompUnit irnode) {
        IRCompUnit optimizedCompUnit = new IRCompUnit(irnode.name());
        for (IRFuncDecl funcDecl : irnode.functions().values()) {
            IRGraph irGraph = buildCFG(funcDecl);
            AvailableExprsDFA availableExprsDFA = new AvailableExprsDFA(irGraph);
            availableExprsDFA.runWorklistAlgo();

            HashMap<IRExpr, String> tempExprMap = new HashMap<>();
            for (Graph<IRStmt>.Node n : irGraph.getAllNodes()) {
                IRSeq seq = new IRSeq();
                for (IRExpr e : availableExprsDFA.exprsGeneratedBy(n)) {
                    String tmp = newTemp();
                    tempExprMap.put(e, tmp);
                    seq.stmts().add(new IRMove(new IRTemp(tmp), e));
                    IRStmt nodestmt = irGraph.getStmt(n);
                    if (nodestmt instanceof IRSeq) {
                        seq.stmts().addAll(((IRSeq) nodestmt).stmts());
                    }
                    else seq.stmts().add(nodestmt);
                }

                seq.stmts().add(visit(irGraph.getStmt(n), tempExprMap));
                irGraph.setStmt(n, seq);
            }

            IRFuncDecl optimizedFuncDecl = new IRFuncDecl(funcDecl.name(),
                    flattenCFG((IRGraph) availableExprsDFA.getGraph()));
            optimizedCompUnit.functions().put(funcDecl.name(), optimizedFuncDecl);
        }
        return optimizedCompUnit;
    }

    public IRStmt visit(IRStmt stmt, HashMap<IRExpr, String> exprTempMap) {
        if (stmt instanceof IRCJump) return visit((IRCJump) stmt, exprTempMap);
        if (stmt instanceof IRExp) return visit((IRExp) stmt, exprTempMap);
        if (stmt instanceof IRJump) return visit((IRJump) stmt, exprTempMap);
        if (stmt instanceof IRMove) return visit((IRMove) stmt, exprTempMap);
        if (stmt instanceof IRReturn) return visit((IRReturn) stmt, exprTempMap);
        if (stmt instanceof IRSeq) return visit((IRSeq) stmt, exprTempMap);
        return stmt;
    }

    public IRExpr visit(IRExpr expr, HashMap<IRExpr, String> exprTempMap) {
        if (expr instanceof IRBinOp) return visit((IRBinOp) expr, exprTempMap);
        if (expr instanceof IRCall) return visit((IRCall) expr, exprTempMap);
        if (expr instanceof IRESeq) return visit((IRESeq) expr, exprTempMap);
        if (expr instanceof IRMem) return visit((IRMem) expr, exprTempMap);
        return expr;
    }

    public IRExpr visit(IRBinOp expr, HashMap<IRExpr, String> exprTempMap) {
        IRExpr newleft;
        IRExpr newright;
        if (exprTempMap.containsKey(expr.left())) {
            newleft = new IRTemp(exprTempMap.get(expr.left()));
        }
        else newleft = visit(expr.left(), exprTempMap);
        if (exprTempMap.containsKey(expr.right())) {
            newright = new IRTemp(exprTempMap.get(expr.right()));
        }
        else newright = visit(expr.right(), exprTempMap);
        return new IRBinOp(expr.opType(), newleft, newright);
    }

    public IRExpr visit(IRCall expr, HashMap<IRExpr, String> exprTempMap) {
        if (exprTempMap.containsKey(expr.target())) {
            return new IRCall(new IRTemp(exprTempMap.get(expr.target())));
        }
        else return new IRCall(visit(expr.target(), exprTempMap));
    }

    public IRStmt visit(IRCJump stmt, HashMap<IRExpr, String> exprTempMap) {
            return stmt.hasFalseLabel() ?
                    new IRCJump(visit(stmt.cond(), exprTempMap),
                        stmt.trueLabel(), stmt.falseLabel())
                    :
                    new IRCJump(visit(stmt.cond(), exprTempMap),
                            stmt.trueLabel())
                    ;
    }


    public IRExpr visit(IRESeq expr, HashMap<IRExpr, String> exprTempMap) {
        if (exprTempMap.containsKey(expr.expr())) {
            return new IRESeq(visit(expr.stmt(), exprTempMap),
                    new IRTemp(exprTempMap.get(expr.expr())));
        }
        else {
            return new IRESeq(visit(expr.stmt(), exprTempMap),
                    visit(expr.expr(), exprTempMap));
        }
    }

    public IRStmt visit(IRExp stmt, HashMap<IRExpr, String> exprTempMap) {
        return new IRExp(visit(stmt.expr(), exprTempMap));
    }

    public IRStmt visit(IRJump stmt, HashMap<IRExpr, String> exprTempMap) {
        return new IRJump(visit(stmt.target(), exprTempMap));
    }

    public IRExpr visit(IRMem expr, HashMap<IRExpr, String> exprTempMap) {
        if (exprTempMap.containsKey(expr.expr())) {
            return new IRMem(new IRTemp(exprTempMap.get(expr.expr())));
        }
        else return new IRMem(visit(expr.expr(), exprTempMap));
    }

    public IRStmt visit(IRMove stmt, HashMap<IRExpr, String> exprTempMap) {
        return new IRMove(visit(stmt.target(), exprTempMap),
                visit(stmt.source(), exprTempMap));
    }

    public IRStmt visit(IRReturn stmt, HashMap<IRExpr, String> exprTempMap) {
        List<IRExpr> rets = new ArrayList<>();
        for (IRExpr e : stmt.rets()) {
            rets.add(visit(e, exprTempMap));
        }
        return new IRReturn(rets);
    }

    public IRStmt visit(IRSeq stmt, HashMap<IRExpr, String> exprTempMap) {
        IRSeq retseq = new IRSeq();
        for (IRStmt s : stmt.stmts()) {
            retseq.stmts().add(visit(s, exprTempMap));
        }
        return retseq;
    }



}
