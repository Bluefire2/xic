package asm;

public class ASMExprBinOpAdd extends ASMExprBinOp {

    public ASMExprBinOpAdd(ASMExpr left, ASMExpr right){
        super(left, right);
        if (left == null || left instanceof ASMExprMem
                || right == null || right instanceof ASMExprMem) {
            throw new IllegalAccessError("Illegal argument of ASM expr");
        }
    }

    @Override
    public String toString() {
        //if RHS is negative don't print the +
        if (this.getRight() instanceof ASMExprConst && ((ASMExprConst)this.getRight()).getVal() < 0){
            return getLeft().toString() + getRight().toString();
        }
        return getLeft().toString() + " + " + getRight().toString();
    }
}
