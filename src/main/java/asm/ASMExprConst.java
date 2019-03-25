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
}
