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

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ASMExprReg) {
            return this.reg.equals(((ASMExprReg) obj).reg);
        }
        return false;
    }
}
