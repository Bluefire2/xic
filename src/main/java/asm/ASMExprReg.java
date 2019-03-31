package asm;

public class ASMExprReg extends ASMExpr {
    private String reg;

    public ASMExprReg(String reg) {
        this.reg = reg;
    }

    public String getReg() {
        return reg;
    }

    @Override
    public String toString() {
        return reg;
    }
}
