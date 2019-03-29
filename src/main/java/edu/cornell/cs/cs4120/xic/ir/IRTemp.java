package edu.cornell.cs.cs4120.xic.ir;

import asm.ASMExprTemp;
import asm.ASMInstr;
import edu.cornell.cs.cs4120.util.SExpPrinter;
import edu.cornell.cs.cs4120.xic.ir.visit.ASMTranslationVisitor;

import java.util.List;

/**
 * An intermediate representation for a temporary register
 * TEMP(name)
 */
public class IRTemp extends IRExpr_c {
    private String name;

    /**
     *
     * @param name name of this temporary register
     */
    public IRTemp(String name) {
        this.name = name;
    }

    public String name() {
        return name;
    }

    @Override
    public List<ASMInstr> accept(ASMTranslationVisitor v, ASMExprTemp t) {
        return v.visit(this, t);
    }

    @Override
    public String label() {
        return "TEMP(" + name + ")";
    }

    @Override
    public void printSExp(SExpPrinter p) {
        p.startList();
        p.printAtom("TEMP");
        p.printAtom(name);
        p.endList();
    }

    @Override
    public boolean equals(Object node) {
        return node instanceof IRTemp;
    }
}
