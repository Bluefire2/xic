package asm;

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
}
