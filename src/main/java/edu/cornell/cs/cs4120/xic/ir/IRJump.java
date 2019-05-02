package edu.cornell.cs.cs4120.xic.ir;

import com.google.common.primitives.Longs;
import edu.cornell.cs.cs4120.util.SExpPrinter;
import edu.cornell.cs.cs4120.xic.ir.visit.ASMTranslationVisitor;
import edu.cornell.cs.cs4120.xic.ir.visit.AggregateVisitor;
import edu.cornell.cs.cs4120.xic.ir.visit.IRVisitor;
import kc875.asm.ASMInstr;

import java.util.List;

/**
 * An intermediate representation for a transfer of control
 */
public class IRJump extends IRStmt {
    private IRExpr target;

    /**
     *
     * @param expr the destination of the jump
     */
    public IRJump(IRExpr expr) {
        target = expr;
    }

    public IRExpr target() {
        return target;
    }

    @Override
    public String label() {
        return "JUMP";
    }

    @Override
    public IRNode visitChildren(IRVisitor v) {
        IRExpr expr = (IRExpr) v.visit(this, target);

        if (expr != target) return v.nodeFactory().IRJump(expr);

        return this;
    }

    @Override
    public <T> T aggregateChildren(AggregateVisitor<T> v) {
        T result = v.unit();
        result = v.bind(result, v.visit(target));
        return result;
    }

    @Override
    public List<ASMInstr> accept(ASMTranslationVisitor v) {
        return v.visit(this);
    }

    @Override
    public void printSExp(SExpPrinter p) {
        p.startList();
        p.printAtom("JUMP");
        target.printSExp(p);
        p.endList();
    }

    @Override
    public boolean equals(Object node) {
        if (node instanceof IRJump) {
            return target.equals(((IRJump) node).target);
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        String hs = "6" + Math.abs(target.hashCode());
        Long hl;
        try {
            hl = Long.parseLong(hs);
        }
        catch (NumberFormatException e) {
            hl = Long.parseLong(hs.substring(0,18));
        }
        return Longs.hashCode(hl);
    }
}
