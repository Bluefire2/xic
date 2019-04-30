package edu.cornell.cs.cs4120.xic.ir.visit;

import edu.cornell.cs.cs4120.xic.ir.*;
import edu.cornell.cs.cs4120.xic.ir.dfa.AvailableExprsDFA;
import edu.cornell.cs.cs4120.xic.ir.dfa.IRGraph;
import kc875.cfg.Graph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CommonSubexprElimVisitor {

    private IRGraph irGraph;

    public CommonSubexprElimVisitor() { }

    private int tempcounter;
    public String newTemp() {
        return String.format("_cse_t%d", tempcounter++);
    }

    /**
     * Perform common subexpression elimination
     * @param irnode
     * @return irnode with common subexpressions hoisted and replaced by temps.
     */
    public IRCompUnit removeCommonSubExpressions(IRCompUnit irnode) {
        IRCompUnit optimizedCompUnit = new IRCompUnit(irnode.name());
        for (IRFuncDecl func : irnode.functions().values()) {
            // Get the stmts in body of this func
            IRStmt body = func.body();
            IRSeq stmts = body instanceof IRSeq ? (IRSeq) body : new IRSeq(body);

            // Get the IR graph and run avail expr
            irGraph = new IRGraph(func);
            AvailableExprsDFA availableExprsDFA = new AvailableExprsDFA(irGraph);
            availableExprsDFA.runWorklistAlgo();

            HashMap<IRExpr, String> tempExprMap = new HashMap<>();
            List<IRStmt> listStmt = stmts.stmts();
            for (int i = 0; i < listStmt.size(); ++i) {
                // optimize each stmt in the body
                IRStmt stmt = listStmt.get(i);
                Graph<IRStmt>.Node n = irGraph.getNode(stmt);
                IRSeq seq = new IRSeq();
                for (IRExpr e : availableExprsDFA.exprsGeneratedBy(n)) {
                    String tmp = newTemp();
                    tempExprMap.put(e, tmp);
                    seq.stmts().add(new IRMove(new IRTemp(tmp), e));
                    if (stmt instanceof IRSeq) {
                        seq.stmts().addAll(((IRSeq) stmt).stmts());
                    }
                    else seq.stmts().add(stmt);
                }

                seq.stmts().add(visit(stmt, tempExprMap));
                // replace this stmt with the new one
                listStmt.set(i, seq);
            }

            IRFuncDecl optimizedFuncDecl = new IRFuncDecl(
                    func.name(),
                    removeNestedIRSeqs(new IRSeq(listStmt))
            );
            optimizedCompUnit.functions().put(func.name(), optimizedFuncDecl);
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
