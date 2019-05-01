package kc875.asm;

public class ASMExprReg extends ASMExprRT {
    private String reg;

    public ASMExprReg(String reg) {
        if (reg == null) {
            throw new NullPointerException();
        }
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

    @Override
    public int hashCode() {
        return ("reg_"+reg).hashCode();
    }
}
