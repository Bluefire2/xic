package edu.cornell.cs.cs4120.xic.ir;

import com.google.common.primitives.Longs;
import edu.cornell.cs.cs4120.util.SExpPrinter;
import edu.cornell.cs.cs4120.xic.ir.visit.ASMTranslationVisitor;
import kc875.asm.ASMExprRT;
import kc875.asm.ASMInstr;

import java.util.List;
import java.util.function.Function;

/**
 * An intermediate representation for named memory address
 * NAME(n)
 */
public class IRName extends IRExpr_c {
    private String name;

    /**
     *
     * @param name name of this memory address
     */
    public IRName(String name) {
        this.name = name;
    }

    public String name() {
        return name;
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
        return e.apply(this);
    }

    @Override
    public String label() {
        return "NAME(" + name + ")";
    }

    @Override
    public void printSExp(SExpPrinter p) {
        p.startList();
        p.printAtom("NAME");
        p.printAtom(name);
        p.endList();
    }

    @Override
    public boolean equals(Object node) {
        if (node instanceof IRName) {
            return name.equals(((IRName) node).name);
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        String hs = "10" + Math.abs(name.hashCode());
        hs = hs.replace("-", "");
        Long hl;
        try {
            hl = Long.parseLong(hs);
        }
        catch (NumberFormatException e) {
            hl = Long.parseLong(hs.substring(0, Math.min(18, hs.length())));
        }
        return Longs.hashCode(hl);
    }
}
