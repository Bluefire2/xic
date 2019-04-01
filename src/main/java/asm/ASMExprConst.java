package asm;

public class ASMExprConst extends ASMExpr {
    private Long val;

    public ASMExprConst(long val) {
        this.val = val;
    }

    public Long getVal() {
        return val;
    }

    @Override
    public String toString() {
        return val.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ASMExprConst) {
            return this.val.longValue() == ((ASMExprConst) obj).val.longValue();
        }
        return false;
    }
}
