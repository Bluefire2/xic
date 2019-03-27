package asm;

public class ASMInstrTwoArg extends ASMInstr {
    private ASMExpr dest;
    private ASMExpr src;

    public ASMInstrTwoArg(ASMOpCode opCode, ASMExpr dest, ASMExpr src) {
        super(opCode);
        this.dest = dest;
        this.src = src;
    }

    @Override
    public String toString() {
        return INDENT_TAB + formatOpCode() + " " +
                dest.toString() + ", " + src.toString();
    }
}
