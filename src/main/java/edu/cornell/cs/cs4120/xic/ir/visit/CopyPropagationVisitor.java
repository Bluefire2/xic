package edu.cornell.cs.cs4120.xic.ir.visit;

import edu.cornell.cs.cs4120.xic.ir.*;
import edu.cornell.cs.cs4120.xic.ir.dfa.AvailableCopiesDFA;
import edu.cornell.cs.cs4120.xic.ir.dfa.IRGraph;
import kc875.utils.SetToMap;
import kc875.utils.SetWithInf;
import polyglot.util.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CopyPropagationVisitor {
    public CopyPropagationVisitor() {
    }

    /**
     * Perform copy propagation: given an assignment x=y, replace all subsequent
     * uses of x with y until next definition of x.
     *
     * @param ir IR.
     * @return irnode with copies of variables propagated.
     */
    public IRCompUnit run(IRCompUnit ir) {
        IRCompUnit optimCompUnit = new IRCompUnit(ir.name());
        for (IRFuncDecl f : ir.functions().values()) {
            IRFuncDecl optimF = propagateCopies(f);
            optimCompUnit.functions().put(optimF.name(), optimF);
        }
        return optimCompUnit;
    }

    public IRFuncDecl propagateCopies(IRFuncDecl func) {
        IRGraph graph = new IRGraph(func);
        AvailableCopiesDFA dfa = new AvailableCopiesDFA(graph);
        dfa.runWorklistAlgo();

        Map<IRGraph.Node, SetWithInf<Pair<IRTemp, IRTemp>>> nodeToCopies =
                dfa.getInMap();

        IRStmt body = func.body();
        IRSeq stmts = body instanceof IRSeq ? (IRSeq) body : new IRSeq(body);
        List<IRStmt> optimStmts = new ArrayList<>();

        for (int i = 0; i < stmts.stmts().size(); ++i) {
            IRGraph.Node n = graph.getNode(i);

            optimStmts.add(visit(
                    stmts.stmts().get(i),
                    SetToMap.convert(nodeToCopies.get(n).getSet())
            ));
        }
        return new IRFuncDecl(
                func.name(), removeNestedIRSeqs(new IRSeq(optimStmts))
        );
    }

    private IRSeq removeNestedIRSeqs(IRSeq stmt) {
        List<IRStmt> stmts = new ArrayList<>();
        for (IRStmt s : stmt.stmts()) {
            if (s instanceof IRSeq) {
                stmts.addAll((removeNestedIRSeqs((IRSeq) s)).stmts());
            } else stmts.add(s);
        }
        return new IRSeq(stmts);
    }

    public IRStmt visit(IRStmt stmt, Map<IRTemp, IRTemp> copyMap) {
        if (stmt instanceof IRCJump) return visit((IRCJump) stmt, copyMap);
        if (stmt instanceof IRExp) return visit((IRExp) stmt, copyMap);
        if (stmt instanceof IRJump) return visit((IRJump) stmt, copyMap);
        if (stmt instanceof IRMove) return visit((IRMove) stmt, copyMap);
        if (stmt instanceof IRReturn) return visit((IRReturn) stmt, copyMap);
        if (stmt instanceof IRSeq) return visit((IRSeq) stmt, copyMap);
        return stmt;
    }

    public IRExpr visit(IRExpr expr, Map<IRTemp, IRTemp> copyMap) {
        if (expr instanceof IRBinOp) return visit((IRBinOp) expr, copyMap);
        if (expr instanceof IRCall) return visit((IRCall) expr, copyMap);
        if (expr instanceof IRMem) return visit((IRMem) expr, copyMap);
        if (expr instanceof IRTemp) return visit((IRTemp) expr, copyMap);
        return expr;
    }

    public IRExpr visit(IRBinOp expr, Map<IRTemp, IRTemp> copyMap) {
        IRExpr newleft;
        IRExpr newright;
        IRExpr left = expr.left();
        IRExpr right = expr.right();
        if (left instanceof IRTemp &&
                copyMap.containsKey(left)) {
            newleft = copyMap.get(left);
        } else newleft = visit(left, copyMap);
        if (right instanceof IRTemp &&
                copyMap.containsKey(right)) {
            newright = copyMap.get(right);
        } else newright = visit(right, copyMap);
        return new IRBinOp(expr.opType(), newleft, newright);
    }

    public IRExpr visit(IRCall expr, Map<IRTemp, IRTemp> copyMap) {
        IRExpr target = expr.target();
        List<IRExpr> newArgs = expr.args().stream()
                .map(arg -> visit(arg, copyMap))
                .collect(Collectors.toList());
        if (target instanceof IRTemp && copyMap.containsKey(target)) {
            return new IRCall(copyMap.get(target), newArgs);
        } else return new IRCall(visit(target, copyMap), newArgs);
    }

    public IRStmt visit(IRCJump stmt, Map<IRTemp, IRTemp> copyMap) {
        return stmt.hasFalseLabel() ?
                new IRCJump(visit(stmt.cond(), copyMap),
                        stmt.trueLabel(), stmt.falseLabel())
                :
                new IRCJump(visit(stmt.cond(), copyMap),
                        stmt.trueLabel())
                ;
    }


    public IRStmt visit(IRExp stmt, Map<IRTemp, IRTemp> copyMap) {
        return new IRExp(visit(stmt.expr(), copyMap));
    }

    public IRStmt visit(IRJump stmt, Map<IRTemp, IRTemp> copyMap) {
        return new IRJump(visit(stmt.target(), copyMap));
    }

    public IRExpr visit(IRMem expr, Map<IRTemp, IRTemp> copyMap) {
        IRExpr target = expr.expr();
        if (target instanceof IRTemp && copyMap.containsKey(target)) {
            return new IRMem(copyMap.get(target));
        } else return new IRMem(visit(target, copyMap));
    }

    public IRStmt visit(IRMove stmt, Map<IRTemp, IRTemp> copyMap) {
        return new IRMove(visit(stmt.target(), copyMap),
                visit(stmt.source(), copyMap));
    }

    public IRStmt visit(IRReturn stmt, Map<IRTemp, IRTemp> copyMap) {
        List<IRExpr> rets = new ArrayList<>();
        for (IRExpr e : stmt.rets()) {
            rets.add(visit(e, copyMap));
        }
        return new IRReturn(rets);
    }

    public IRStmt visit(IRSeq stmt, Map<IRTemp, IRTemp> copyMap) {
        IRSeq retseq = new IRSeq();
        for (IRStmt s : stmt.stmts()) {
            retseq.stmts().add(visit(s, copyMap));
        }
        return retseq;
    }

    public IRExpr visit(IRTemp expr, Map<IRTemp, IRTemp> copyMap) {
        return copyMap.getOrDefault(expr, expr);
    }
}
