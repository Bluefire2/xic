package asm;

public class ASMInstrJump extends ASMInstr {
    private String label;

    public ASMInstrJump(ASMOpCode opCode, String label) {
        super(opCode);
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public String toString() {
        return INDENT_TAB + formatOpCode() + " " + label;
    }
}
