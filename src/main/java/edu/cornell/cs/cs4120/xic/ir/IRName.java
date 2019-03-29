package edu.cornell.cs.cs4120.xic.ir;

import asm.ASMExprTemp;
import asm.ASMInstr;
import edu.cornell.cs.cs4120.util.SExpPrinter;
import edu.cornell.cs.cs4120.xic.ir.visit.ASMTranslationVisitor;

import java.util.List;

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
    public List<ASMInstr> accept(ASMTranslationVisitor v, ASMExprTemp t) {
        throw new IllegalAccessError("IRName should not be visited by ASM " +
                "Translator");
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
}
