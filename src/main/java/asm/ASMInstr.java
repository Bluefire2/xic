package asm;

import asm.visit.RegAllocationNaiveVisitor;

import java.util.List;

public abstract class ASMInstr {
    private ASMOpCode opCode;
    // String for a tab indent when this instruction is output to an asm file
    static final String INDENT_TAB = "    ";

    ASMInstr(ASMOpCode opCode) {
        this.opCode = opCode;
    }

    String formatOpCode() {
        return opCode.toString();
    }

    public ASMOpCode getOpCode() {
        return opCode;
    }

    public abstract List<ASMInstr> accept(RegAllocationNaiveVisitor v);

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ASMInstr) {
            return this.opCode.equals(((ASMInstr) obj).opCode);
        }
        return false;
    }
}
