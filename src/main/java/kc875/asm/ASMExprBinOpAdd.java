package kc875.asm;

public class ASMExprBinOpAdd extends ASMExprBinOp {

    public ASMExprBinOpAdd(ASMExpr left, ASMExpr right){
        super(left, right);
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
