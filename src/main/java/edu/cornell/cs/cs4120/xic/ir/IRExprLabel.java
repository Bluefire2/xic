package edu.cornell.cs.cs4120.xic.ir;

import com.google.common.primitives.Longs;
import edu.cornell.cs.cs4120.util.SExpPrinter;
import edu.cornell.cs.cs4120.xic.ir.visit.ASMTranslationVisitor;
import kc875.asm.ASMExprRT;
import kc875.asm.ASMInstr;

import java.util.List;
import java.util.function.Function;


//NOTE this represents a memory location
//cannot be used as an operand in ASM,
// if you need the contents use [name]
// if you want the location use lea some_temp [name]
public class IRExprLabel extends IRExpr_c {
    private String name;

    public IRExprLabel(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public <T> T matchLow(
            Function<IRBinOp, T> a,
            Function<IRCall, T> b,
            Function<IRConst, T> c,
            Function<IRMem, T> d,
            Function<IRName, T> e,
            Function<IRTemp, T> f,
            Function<IRExprLabel, T> g) {
        return null;
    }

    @Override
    public String label() {
        return "EXPR_LABEL(" + name + ")";
    }


    @Override
    public List<ASMInstr> accept(ASMTranslationVisitor v, ASMExprRT t) {
        return v.visit(this, t);
    }

    @Override
    public void printSExp(SExpPrinter p) {
        p.startUnifiedList();
        p.printAtom("EXPR_LABEL");
        p.printAtom(name);
        p.endList();
    }

    @Override
    public boolean equals(Object node) {
        if (node instanceof IRExprLabel) {
            return name.equals(((IRExprLabel) node).getName());
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        String hs = "7" + Math.abs(name.hashCode());
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
