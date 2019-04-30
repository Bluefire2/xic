package edu.cornell.cs.cs4120.xic.ir.visit;

import edu.cornell.cs.cs4120.xic.ir.*;
import edu.cornell.cs.cs4120.xic.ir.dfa.IRGraph;
import edu.cornell.cs.cs4120.xic.ir.dfa.ReachingDefnsDFA;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CopyPropagationVisitor {
    private IRGraph irGraph;

    public CopyPropagationVisitor() {
    }

    /**
     * Perform copy propagation: given an assignment x=y, replace all subsequent
     * uses of x with y until next definition of x.
     *
     * @param irnode
     * @return irnode with copies of variables propagated.
     */
    public IRCompUnit propagateCopies(IRCompUnit irnode) {
        IRCompUnit optimizedCompUnit = new IRCompUnit(irnode.name());
        for (IRFuncDecl funcDecl : irnode.functions().values()) {
            irGraph = new IRGraph(funcDecl);
            ReachingDefnsDFA reachingDefnsDFA = new ReachingDefnsDFA(irGraph);
            reachingDefnsDFA.runWorklistAlgo();

            HashMap<String, IRGraph.Node> defnNodeMap = new HashMap<>();
            HashMap<String, String> copyMap = new HashMap<>();

            IRStmt body = funcDecl.body();
            IRSeq stmts = body instanceof IRSeq ? (IRSeq) body : new IRSeq(body);

            List<IRStmt> listStmts = stmts.stmts();
            for (int i = 0; i < listStmts.size(); i++) {
                IRSeq seq = new IRSeq();
                IRStmt s = listStmts.get(i);
                IRGraph.Node n = irGraph.getNode(s);
                if (s instanceof IRMove) {
                    IRExpr target = ((IRMove) s).target();
                    IRExpr source = ((IRMove) s).source();
                    if (target instanceof IRTemp) {
                        String tname = ((IRTemp) target).name();
                        defnNodeMap.put(tname, n);
                        if (source instanceof IRTemp) {
                            copyMap.put(tname, ((IRTemp) source).name());
                        } else {
                            copyMap.remove(tname);
                        }
                    }
                }

                HashMap<String, String> perNodeCopyMap = new HashMap<>();
                for (String str : copyMap.keySet()) {
                    if (defnNodeMap.containsKey(str)) {
                        IRGraph.Node nprime = defnNodeMap.get(str);
                        if (nprime.equals(n) || nprime.goesTo(n)) {
                            perNodeCopyMap.put(str, copyMap.get(str));
                        }
                    }
                }
                seq.stmts().add(visit(s, perNodeCopyMap));
                listStmts.set(i, seq);
            }

            IRFuncDecl optimizedFuncDecl = new IRFuncDecl(funcDecl.name(),
                    new IRSeq(listStmts));
            optimizedCompUnit.functions().put(funcDecl.name(), optimizedFuncDecl);
        }
        return optimizedCompUnit;
    }

    public IRStmt visit(IRStmt stmt, HashMap<String, String> copyMap) {
        if (stmt instanceof IRCJump) return visit((IRCJump) stmt, copyMap);
        if (stmt instanceof IRExp) return visit((IRExp) stmt, copyMap);
        if (stmt instanceof IRJump) return visit((IRJump) stmt, copyMap);
        if (stmt instanceof IRMove) return visit((IRMove) stmt, copyMap);
        if (stmt instanceof IRReturn) return visit((IRReturn) stmt, copyMap);
        if (stmt instanceof IRSeq) return visit((IRSeq) stmt, copyMap);
        return stmt;
    }

    public IRExpr visit(IRExpr expr, HashMap<String, String> copyMap) {
        if (expr instanceof IRBinOp) return visit((IRBinOp) expr, copyMap);
        if (expr instanceof IRCall) return visit((IRCall) expr, copyMap);
        if (expr instanceof IRESeq) return visit((IRESeq) expr, copyMap);
        if (expr instanceof IRMem) return visit((IRMem) expr, copyMap);
        return expr;
    }

    public IRExpr visit(IRBinOp expr, HashMap<String, String> copyMap) {
        IRExpr newleft;
        IRExpr newright;
        IRExpr left = expr.left();
        IRExpr right = expr.right();
        if (left instanceof IRTemp &&
                copyMap.containsKey(((IRTemp) left).name())) {
            newleft = new IRTemp(copyMap.get(((IRTemp) left).name()));
        } else newleft = visit(left, copyMap);
        if (right instanceof IRTemp &&
                copyMap.containsKey(((IRTemp) right).name())) {
            newright = new IRTemp(copyMap.get(((IRTemp) right).name()));
        } else newright = visit(right, copyMap);
        return new IRBinOp(expr.opType(), newleft, newright);
    }

    public IRExpr visit(IRCall expr, HashMap<String, String> copyMap) {
        IRExpr target = expr.target();
        if (target instanceof IRTemp &&
                copyMap.containsKey(((IRTemp) target).name())) {
            return new IRCall(new IRTemp(copyMap.get(((IRTemp) target).name())));
        } else return new IRCall(visit(target, copyMap));
    }

    public IRStmt visit(IRCJump stmt, HashMap<String, String> copyMap) {
        return stmt.hasFalseLabel() ?
                new IRCJump(visit(stmt.cond(), copyMap),
                        stmt.trueLabel(), stmt.falseLabel())
                :
                new IRCJump(visit(stmt.cond(), copyMap),
                        stmt.trueLabel())
                ;
    }


    public IRExpr visit(IRESeq expr, HashMap<String, String> copyMap) {
        IRExpr nestedExpr = expr.expr();
        if (nestedExpr instanceof IRTemp &&
                copyMap.containsKey(((IRTemp) nestedExpr).name())) {
            return new IRESeq(visit(expr.stmt(), copyMap),
                    new IRTemp(copyMap.get(((IRTemp) nestedExpr).name())));
        } else {
            return new IRESeq(visit(expr.stmt(), copyMap),
                    visit(nestedExpr, copyMap));
        }
    }

    public IRStmt visit(IRExp stmt, HashMap<String, String> copyMap) {
        return new IRExp(visit(stmt.expr(), copyMap));
    }

    public IRStmt visit(IRJump stmt, HashMap<String, String> copyMap) {
        return new IRJump(visit(stmt.target(), copyMap));
    }

    public IRExpr visit(IRMem expr, HashMap<String, String> copyMap) {
        IRExpr target = expr.expr();
        if (target instanceof IRTemp &&
                copyMap.containsKey(((IRTemp) target).name())) {
            return new IRMem(new IRTemp(copyMap.get(((IRTemp) target).name())));
        } else return new IRMem(visit(target, copyMap));
    }

    public IRStmt visit(IRMove stmt, HashMap<String, String> copyMap) {
        return new IRMove(visit(stmt.target(), copyMap),
                visit(stmt.source(), copyMap));
    }

    public IRStmt visit(IRReturn stmt, HashMap<String, String> copyMap) {
        List<IRExpr> rets = new ArrayList<>();
        for (IRExpr e : stmt.rets()) {
            rets.add(visit(e, copyMap));
        }
        return new IRReturn(rets);
    }

    public IRStmt visit(IRSeq stmt, HashMap<String, String> copyMap) {
        IRSeq retseq = new IRSeq();
        for (IRStmt s : stmt.stmts()) {
            retseq.stmts().add(visit(s, copyMap));
        }
        return retseq;
    }

    public IRGraph getIrGraph() {
        return irGraph;
    }
}
