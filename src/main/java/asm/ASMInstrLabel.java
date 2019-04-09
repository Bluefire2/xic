package asm;

import asm.visit.RegAllocationNaiveVisitor;

import java.util.List;

public class ASMInstrLabel extends ASMInstr {
    private String name;

    public ASMInstrLabel(String name) {
        super(ASMOpCode.LABEL);
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name + ":";
    }

    @Override
    public List<ASMInstr> accept(RegAllocationNaiveVisitor v) {
        return v.visit(this);
    }
}
