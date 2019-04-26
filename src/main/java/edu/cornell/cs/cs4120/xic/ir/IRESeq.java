package edu.cornell.cs.cs4120.xic.ir;

import edu.cornell.cs.cs4120.util.SExpPrinter;
import edu.cornell.cs.cs4120.xic.ir.visit.ASMTranslationVisitor;
import edu.cornell.cs.cs4120.xic.ir.visit.AggregateVisitor;
import edu.cornell.cs.cs4120.xic.ir.visit.CheckCanonicalIRVisitor;
import edu.cornell.cs.cs4120.xic.ir.visit.IRVisitor;
import kc875.asm.ASMExprRT;
import kc875.asm.ASMInstr;

import java.util.List;
import java.util.function.Function;

/**
 * An intermediate representation for an expression evaluated under side effects
 * ESEQ(stmt, expr)
 */
public class IRESeq extends IRExpr_c {
    private IRStmt stmt;
    private IRExpr expr;
    private boolean replaceParent;

    /**
     *
     * @param stmt IR statement to be evaluated for side effects
     * @param expr IR expression to be evaluated after {@code stmt}
     */
    public IRESeq(IRStmt stmt, IRExpr expr) {
        this.stmt = stmt;
        this.expr = expr;
        this.replaceParent = false;
    }

    public IRESeq(IRStmt stmt, IRExpr expr, boolean replaceParent) {
        this.stmt = stmt;
        this.expr = expr;
        this.replaceParent = replaceParent;
    }

    public IRStmt stmt() {
        return stmt;
    }

    public IRExpr expr() {
        return expr;
    }

    @Override
    public String label() {
        return "ESEQ";
    }

    @Override
    public IRNode visitChildren(IRVisitor v) {
        IRStmt stmt = (IRStmt) v.visit(this, this.stmt);
        IRExpr expr = (IRExpr) v.visit(this, this.expr);

        if (expr != this.expr || stmt != this.stmt)
            return v.nodeFactory().IRESeq(stmt, expr);

        return this;
    }

    @Override
    public <T> T aggregateChildren(AggregateVisitor<T> v) {
        T result = v.unit();
        result = v.bind(result, v.visit(stmt));
        result = v.bind(result, v.visit(expr));
        return result;
    }

    @Override
    public boolean isCanonical(CheckCanonicalIRVisitor v) {
        return false;
    }

    @Override
    public List<ASMInstr> accept(ASMTranslationVisitor v, ASMExprRT t) {
        throw new IllegalAccessError("IRESeq should not be visited by ASM " +
                "Translator");
    }

    @Override
    public <T> T matchLow(Function<IRBinOp, T> a,
                          Function<IRCall, T> b,
                          Function<IRConst, T> c,
                          Function<IRMem, T> d,
                          Function<IRName, T> e,
                          Function<IRTemp, T> f) {
        throw new IllegalAccessError("IRESeq can't be matched upon by the " +
                "lowered IR matcher");
    }

    @Override
    public void printSExp(SExpPrinter p) {
        p.startList();
        p.printAtom("ESEQ");
        stmt.printSExp(p);
        expr.printSExp(p);
        p.endList();
    }

    @Override
    public boolean equals(Object node) {
        if (node instanceof IRESeq) {
            IRESeq ireSeq = (IRESeq) node;
            return expr.equals(ireSeq.expr)
                    && stmt.equals(ireSeq.stmt);
        } else {
            return false;
        }
    }

    public boolean isReplaceParent() {
        return replaceParent;
    }

    public void setReplaceParent(boolean replaceParent) {
        this.replaceParent = replaceParent;
    }
}
