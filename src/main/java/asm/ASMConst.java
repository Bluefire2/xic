package asm;

public class ASMConst extends ASMExpr {
    private Long val;

    ASMConst(long l) {
        this.val = l;
    }

    public Long getVal() {
        return val;
    }

    @Override
    public String toString(){
        return val.toString();
    }
}
