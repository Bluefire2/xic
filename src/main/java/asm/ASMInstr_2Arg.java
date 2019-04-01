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

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ASMInstr_2Arg) {
            ASMInstr_2Arg o = (ASMInstr_2Arg) obj;
            return this.getOpCode().equals(o.getOpCode())
                    && this.dest.equals(o.dest)
                    && this.src.equals(o.src);
        }
        return false;
    }
}
