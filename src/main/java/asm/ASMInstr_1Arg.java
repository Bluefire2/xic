package asm;

public class ASMInstr_1Arg extends ASMInstr {
    private ASMExpr arg;

    public ASMInstr_1Arg(ASMOpCode opCode, ASMExpr arg) {
        super(opCode);
        this.arg = arg;
    }

    @Override
    public String toString() {
        return INDENT_TAB + formatOpCode() + " " + arg.toString();
    }
}
