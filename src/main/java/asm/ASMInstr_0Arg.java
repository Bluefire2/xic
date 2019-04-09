package asm;

import asm.visit.RegAllocationNaiveVisitor;

import java.util.List;

public class ASMInstr_0Arg extends ASMInstr {

    public ASMInstr_0Arg(ASMOpCode opCode) {
        super(opCode);
    }

    @Override
    public List<ASMInstr> accept(RegAllocationNaiveVisitor v) {
        return v.visit(this);
    }
}
