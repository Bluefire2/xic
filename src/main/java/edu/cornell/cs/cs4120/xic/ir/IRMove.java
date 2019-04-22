package edu.cornell.cs.cs4120.xic.ir;

import edu.cornell.cs.cs4120.util.SExpPrinter;
import edu.cornell.cs.cs4120.xic.ir.visit.ASMTranslationVisitor;
import edu.cornell.cs.cs4120.xic.ir.visit.AggregateVisitor;
import edu.cornell.cs.cs4120.xic.ir.visit.IRVisitor;
import kc875.asm.ASMInstr;

import java.util.List;

/**
 * An intermediate representation for a move statement
 * MOVE(target, expr)
 */
public class IRMove extends IRStmt {
    private IRExpr target;
    private IRExpr src;
    public boolean istrash;

    /**
     *
     * @param target the destination of this move
     * @param src the expression whose value is to be moved
     */
    public IRMove(IRExpr target, IRExpr src) {
        this.target = target;
        this.src = src;
        this.istrash = false;
    }

    public IRMove(IRExpr target, IRExpr src, boolean trash) {
        this.target = target;
        this.src = src;
        this.istrash = trash;
    }

    public IRExpr target() {
        return target;
    }

    public IRExpr source() {
        return src;
    }

    public boolean isIstrash() {
        return istrash;
    }

    @Override
    public String label() {
        return "MOVE";
    }

    @Override
    public IRNode visitChildren(IRVisitor v) {
        IRExpr target = (IRExpr) v.visit(this, this.target);
        IRExpr expr = (IRExpr) v.visit(this, src);

        if (target != this.target || expr != src)
            return v.nodeFactory().IRMove(target, expr);

        return this;
    }

    @Override
    public <T> T aggregateChildren(AggregateVisitor<T> v) {
        T result = v.unit();
        result = v.bind(result, v.visit(target));
        result = v.bind(result, v.visit(src));
        return result;
    }

    @Override
    public List<ASMInstr> accept(ASMTranslationVisitor v) {
        return v.visit(this);
    }

    @Override
    public void printSExp(SExpPrinter p) {
        p.startList();
        p.printAtom("MOVE");
        target.printSExp(p);
        src.printSExp(p);
        p.endList();
    }

    @Override
    public boolean equals(Object node) {
        if (node instanceof IRMove) {
            IRMove irMove = (IRMove) node;
            return target.equals(irMove.target)
                    && src.equals(irMove.src);
        } else {
            return false;
        }
    }
}
