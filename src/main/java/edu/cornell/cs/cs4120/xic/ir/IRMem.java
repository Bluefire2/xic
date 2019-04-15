package edu.cornell.cs.cs4120.xic.ir;

import asm.ASMExprRegReplaceable;
import asm.ASMInstr;
import edu.cornell.cs.cs4120.util.InternalCompilerError;
import edu.cornell.cs.cs4120.util.SExpPrinter;
import edu.cornell.cs.cs4120.xic.ir.visit.ASMTranslationVisitor;
import edu.cornell.cs.cs4120.xic.ir.visit.AggregateVisitor;
import edu.cornell.cs.cs4120.xic.ir.visit.IRVisitor;

import java.util.List;
import java.util.function.Function;

/**
 * An intermediate representation for a memory location
 * MEM(e)
 */
public class IRMem extends IRExpr_c {
    public enum MemType {
        NORMAL, IMMUTABLE;

        @Override
        public String toString() {
            switch (this) {
            case NORMAL:
                return "MEM";
            case IMMUTABLE:
                return "MEM_I";
            }
            throw new InternalCompilerError("Unknown mem type!");
        }
    };

    private IRExpr expr;
    private MemType memType;

    /**
     *
     * @param expr the address of this memory location
     */
    public IRMem(IRExpr expr) {
        this(expr, MemType.NORMAL);
    }

    public IRMem(IRExpr expr, MemType memType) {
        this.expr = expr;
        this.memType = memType;
    }

    public IRExpr expr() {
        return expr;
    }

    public MemType memType() {
        return memType;
    }

    @Override
    public String label() {
        return memType.toString();
    }

    @Override
    public IRNode visitChildren(IRVisitor v) {
        IRExpr expr = (IRExpr) v.visit(this, this.expr);

        if (expr != this.expr) return v.nodeFactory().IRMem(expr);

        return this;
    }

    @Override
    public <T> T aggregateChildren(AggregateVisitor<T> v) {
        T result = v.unit();
        result = v.bind(result, v.visit(expr));
        return result;
    }

    @Override
    public List<ASMInstr> accept(ASMTranslationVisitor v, ASMExprRegReplaceable t) {
        return v.visit(this, t);
    }

    @Override
    public <T> T matchLow(Function<IRBinOp, T> a,
                          Function<IRCall, T> b,
                          Function<IRConst, T> c,
                          Function<IRMem, T> d,
                          Function<IRName, T> e,
                          Function<IRTemp, T> f) {
        return d.apply(this);
    }

    @Override
    public void printSExp(SExpPrinter p) {
        p.startList();
        p.printAtom(memType.toString());
        expr.printSExp(p);
        p.endList();
    }

    @Override
    public boolean equals(Object node) {
        if (node instanceof IRMem) {
            IRMem irMem = (IRMem) node;
            return expr.equals(irMem.expr);
        } else {
            return false;
        }
    }
}
