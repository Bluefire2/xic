package edu.cornell.cs.cs4120.xic.ir;

import com.google.common.primitives.Longs;
import edu.cornell.cs.cs4120.util.SExpPrinter;
import edu.cornell.cs.cs4120.xic.ir.visit.ASMTranslationVisitor;
import kc875.asm.ASMExprRT;
import kc875.asm.ASMInstr;

import java.util.List;
import java.util.function.Function;

/**
 * An intermediate representation for a 64-bit integer constant.
 * CONST(n)
 */
public class IRConst extends IRExpr_c {
    private long value;

    /**
     *
     * @param value value of this constant
     */
    public IRConst(long value) {
        this.value = value;
    }

    public long value() {
        return value;
    }

    @Override
    public List<ASMInstr> accept(ASMTranslationVisitor v, ASMExprRT t) {
        return v.visit(this, t);
    }

    @Override
    public <T> T matchLow(Function<IRBinOp, T> a,
                          Function<IRCall, T> b,
                          Function<IRConst, T> c,
                          Function<IRMem, T> d,
                          Function<IRName, T> e,
                          Function<IRTemp, T> f) {
        return c.apply(this);
    }

    @Override
    public String label() {
        return "CONST(" + value + ")";
    }

    @Override
    public boolean isConstant() {
        return true;
    }

    @Override
    public long constant() {
        return value;
    }

    @Override
    public void printSExp(SExpPrinter p) {
        p.startList();
        p.printAtom("CONST");
        p.printAtom(String.valueOf(value));
        p.endList();
    }

    @Override
    public boolean equals(Object node) {
        if (node instanceof IRConst) {
            return value == ((IRConst) node).value;
        } else {
            return false;
        }
    }

    public int hashCode() {
        String hs = "3" + value;
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
