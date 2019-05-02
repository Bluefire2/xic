package edu.cornell.cs.cs4120.xic.ir.visit;

import edu.cornell.cs.cs4120.xic.ir.*;
import edu.cornell.cs.cs4120.xic.ir.dfa.AvailableExprsDFA;
import edu.cornell.cs.cs4120.xic.ir.dfa.IRGraph;
import kc875.cfg.Graph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CommonSubexprElimVisitor {

    public CommonSubexprElimVisitor() { }

    private int tempcounter;
    private String newTemp() {
        return String.format("_cse_t%d", tempcounter++);
    }

    HashMap<String, Boolean> tempUsedMap = new HashMap<>();

    /**
     * Perform common subexpression elimination
     * @param ir irnode
     * @return irnode with common subexpressions hoisted and replaced by temps.
     */
    public IRCompUnit run(IRCompUnit ir) {
        IRCompUnit optimCompUnit = new IRCompUnit(ir.name());
        for (IRFuncDecl f : ir.functions().values()) {
            IRFuncDecl optimF = removeCommonSubExpressions(f);
            optimCompUnit.functions().put(optimF.name(), optimF);
        }
        return optimCompUnit;
    }

    private IRFuncDecl removeCommonSubExpressions(IRFuncDecl func) {
        IRGraph irGraph = new IRGraph(func);
        AvailableExprsDFA dfa = new AvailableExprsDFA(irGraph);
        dfa.runWorklistAlgo();

        IRStmt body = func.body();
        IRSeq stmts = body instanceof IRSeq ? (IRSeq) body : new IRSeq(body);

        // Get the IR graph and run avail expr
        Map<IRExpr, String> tempExprMap = new HashMap<>();
        List<IRStmt> listStmt = stmts.stmts();
        for (int i = 0; i < listStmt.size(); ++i) {
            // optimize each stmt in the body
            IRStmt stmt = listStmt.get(i);
            Graph<IRStmt>.Node n = irGraph.getNode(i);
            IRSeq seq = new IRSeq();
            for (IRExpr e : dfa.exprsGeneratedBy(n)) {
                if (e instanceof IRTemp
                        || e instanceof IRConst
                        || tempExprMap.containsKey(e)) {
                    continue;
                } else if (dfa.nodesUsingExpr(e).size() > 1) {
                    String tmp = newTemp();
                    tempExprMap.put(e, tmp);
                    tempUsedMap.put(tmp, Boolean.FALSE);
                    seq.stmts().add(new IRMove(new IRTemp(tmp), e));
                }
            }
            seq.stmts().add(visit(stmt, tempExprMap, tempUsedMap));

            // replace this stmt with the new one
            listStmt.set(i, seq);
        }

        //Optimize by removing unused movs
        for (int i = 0; i < listStmt.size(); ++i) {
            List<IRStmt> innerStmts = new ArrayList<>();
            IRStmt stmt = listStmt.get(i);
            IRSeq retseq = new IRSeq();
            if (stmt instanceof IRSeq)
                innerStmts.addAll(removeNestedIRSeqs((IRSeq) stmt).stmts());
            else
                innerStmts.add(stmt);
            for (IRStmt s : innerStmts) {
                if (s instanceof IRMove) {
                    IRExpr target = ((IRMove) s).target();
                    if (target instanceof IRTemp) {
                        String tn = ((IRTemp) target).name();
                        if (tempUsedMap.containsKey(tn)) {
                            if (!(tempUsedMap.get(tn))) {
                                continue;
                            }
                        }
                    }
                }
                retseq.stmts().add(s);
            }
            listStmt.set(i, retseq);
        }

        return new IRFuncDecl(
                func.name(), removeNestedIRSeqs(new IRSeq(listStmt))
        );
    }

    private IRSeq removeNestedIRSeqs(IRSeq stmt) {
        List<IRStmt> stmts = new ArrayList<>();
        for (IRStmt s : stmt.stmts()) {
            if (s instanceof IRSeq)
                stmts.addAll((removeNestedIRSeqs((IRSeq) s)).stmts());
            else
                stmts.add(s);
        }
        return new IRSeq(stmts);
    }

    public IRStmt visit(IRStmt stmt, Map<IRExpr, String> exprTempMap, HashMap<String, Boolean> tempUsedMap) {
        if (stmt instanceof IRCJump) return visit((IRCJump) stmt, exprTempMap, tempUsedMap);
        if (stmt instanceof IRExp) return visit((IRExp) stmt, exprTempMap, tempUsedMap);
        if (stmt instanceof IRJump) return visit((IRJump) stmt, exprTempMap, tempUsedMap);
        if (stmt instanceof IRMove) return visit((IRMove) stmt, exprTempMap, tempUsedMap);
        if (stmt instanceof IRReturn) return visit((IRReturn) stmt, exprTempMap, tempUsedMap);
        if (stmt instanceof IRSeq) return visit((IRSeq) stmt, exprTempMap, tempUsedMap);
        return stmt;
    }

    public IRExpr visit(IRExpr expr, Map<IRExpr, String> exprTempMap, HashMap<String, Boolean> tempUsedMap) {
        if (expr instanceof IRBinOp) return visit((IRBinOp) expr, exprTempMap, tempUsedMap);
        if (expr instanceof IRCall) return visit((IRCall) expr, exprTempMap, tempUsedMap);
        if (expr instanceof IRESeq) return visit((IRESeq) expr, exprTempMap, tempUsedMap);
        if (expr instanceof IRMem) return visit((IRMem) expr, exprTempMap, tempUsedMap);
        return expr;
    }

    public IRExpr visit(IRBinOp expr, Map<IRExpr, String> exprTempMap, HashMap<String, Boolean> tempUsedMap) {
        IRExpr newleft;
        IRExpr newright;
        if (exprTempMap.containsKey(expr)) {
            String t = exprTempMap.get(expr);
            tempUsedMap.put(t, Boolean.TRUE);
            return new IRTemp(t);
        }
        else if (exprTempMap.containsKey(expr.left())) {
            String t = exprTempMap.get(expr.left());
            tempUsedMap.put(t, Boolean.TRUE);
            newleft = new IRTemp(t);
        }
        else newleft = visit(expr.left(), exprTempMap, tempUsedMap);
        if (exprTempMap.containsKey(expr.right())) {
            String t = exprTempMap.get(expr.right());
            tempUsedMap.put(t, Boolean.TRUE);
            newright = new IRTemp(t);
        }
        else newright = visit(expr.right(), exprTempMap, tempUsedMap);
        return new IRBinOp(expr.opType(), newleft, newright);
    }

    public IRExpr visit(IRCall expr, Map<IRExpr, String> exprTempMap, HashMap<String, Boolean> tempUsedMap) {
        List<IRExpr> newArgs = expr.args().stream()
                .map(arg -> visit(arg, exprTempMap, tempUsedMap))
                .collect(Collectors.toList());
        if (exprTempMap.containsKey(expr.target())) {
            String t = exprTempMap.get(expr.target());
            tempUsedMap.put(t, Boolean.TRUE);
            return new IRCall(new IRTemp(t), newArgs);
        }
        else return new IRCall(visit(expr.target(), exprTempMap, tempUsedMap), newArgs);
    }

    public IRStmt visit(IRCJump stmt, Map<IRExpr, String> exprTempMap, HashMap<String, Boolean> tempUsedMap) {
            return stmt.hasFalseLabel() ?
                    new IRCJump(visit(stmt.cond(), exprTempMap, tempUsedMap),
                        stmt.trueLabel(), stmt.falseLabel())
                    :
                    new IRCJump(visit(stmt.cond(), exprTempMap, tempUsedMap),
                            stmt.trueLabel())
                    ;
    }


    public IRExpr visit(IRESeq expr, Map<IRExpr, String> exprTempMap, HashMap<String, Boolean> tempUsedMap) {
        if (exprTempMap.containsKey(expr.expr())) {
            String t = exprTempMap.get(expr.expr());
            tempUsedMap.put(t, Boolean.TRUE);
            return new IRESeq(visit(expr.stmt(), exprTempMap, tempUsedMap),
                    new IRTemp(t));
        }
        else {
            return new IRESeq(visit(expr.stmt(), exprTempMap, tempUsedMap),
                    visit(expr.expr(), exprTempMap, tempUsedMap));
        }
    }

    public IRStmt visit(IRExp stmt, Map<IRExpr, String> exprTempMap, HashMap<String, Boolean> tempUsedMap) {
        return new IRExp(visit(stmt.expr(), exprTempMap, tempUsedMap));
    }

    public IRStmt visit(IRJump stmt, Map<IRExpr, String> exprTempMap, HashMap<String, Boolean> tempUsedMap) {
        return new IRJump(visit(stmt.target(), exprTempMap, tempUsedMap));
    }

    public IRExpr visit(IRMem expr, Map<IRExpr, String> exprTempMap, HashMap<String, Boolean> tempUsedMap) {
        if (exprTempMap.containsKey(expr)) {
            String t = exprTempMap.get(expr);
            tempUsedMap.put(t, Boolean.TRUE);
            return new IRTemp(t);
        }
        else if (exprTempMap.containsKey(expr.expr())) {
            String t = exprTempMap.get(expr.expr());
            tempUsedMap.put(t, Boolean.TRUE);
            return new IRMem(new IRTemp(t));
        }
        else return new IRMem(visit(expr.expr(), exprTempMap, tempUsedMap));
    }

    public IRStmt visit(IRMove stmt, Map<IRExpr, String> exprTempMap, HashMap<String, Boolean> tempUsedMap) {
        return new IRMove(visit(stmt.target(), exprTempMap, tempUsedMap),
                visit(stmt.source(), exprTempMap, tempUsedMap));
    }

    public IRStmt visit(IRReturn stmt, Map<IRExpr, String> exprTempMap, HashMap<String, Boolean> tempUsedMap) {
        List<IRExpr> rets = new ArrayList<>();
        for (IRExpr e : stmt.rets()) {
            rets.add(visit(e, exprTempMap, tempUsedMap));
        }
        return new IRReturn(rets);
    }

    public IRStmt visit(IRSeq stmt, Map<IRExpr, String> exprTempMap, HashMap<String, Boolean> tempUsedMap) {
        IRSeq retseq = new IRSeq();
        for (IRStmt s : stmt.stmts()) {
            retseq.stmts().add(visit(s, exprTempMap, tempUsedMap));
        }
        return retseq;
    }

}
