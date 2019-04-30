package kc875.asm;

import kc875.asm.visit.ASMinstrBareVisitor;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ASMInstr_0Arg extends ASMInstr {

    public ASMInstr_0Arg(ASMOpCode opCode) {
        super(opCode);
    }

    @Override
    public List<ASMInstr> accept(ASMinstrBareVisitor<List<ASMInstr>> v) {
        return v.visit(this);
    }

    @Override
    public String toString() {
        return INDENT_TAB + formatOpCode();
    }

    @Override
    public boolean destIsDefButNoUse() {
        return false;
    }

    @Override
    public Set<ASMExprReg> implicitRegs() {
        return new HashSet<>();
    }

    @Override
    public boolean destHasNewDef() {
        return false;
    }
}
