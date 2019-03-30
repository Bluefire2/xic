package asm;

public class ASMInstr_2Arg extends ASMInstr {
    private ASMExpr dest;
    private ASMExpr src;

    public ASMInstr_2Arg(ASMOpCode opCode, ASMExpr dest, ASMExpr src) {
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
