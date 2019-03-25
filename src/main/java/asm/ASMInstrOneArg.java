package asm;

public abstract class ASMInstrOneArg extends ASMInstr {
    private ASMExpr arg;

    ASMInstrOneArg(ASMOpCode opCode, ASMExpr arg) {
        super(opCode);
        this.arg = arg;
    }

    @Override
    public String toString() {
        return INDENT_TAB + formatOpCode() + " " + arg.toString();
    }
}
