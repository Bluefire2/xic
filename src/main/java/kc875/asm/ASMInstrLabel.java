package kc875.asm;

import kc875.asm.visit.ASMinstrBareVisitor;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
    public List<ASMInstr> accept(ASMinstrBareVisitor<List<ASMInstr>> v) {
        return v.visit(this);
    }

    @Override
    public Set<ASMExprReg> implicitDefRegs() {
        return new HashSet<>();
    }

    @Override
    public Set<ASMExprReg> implicitUsedRegs() {
        return new HashSet<>();
    }

    @Override
    public boolean destHasNewDef() {
        return false;
    }

    @Override
    public boolean destIsDefButNoUse() {
        return false;
    }
}
