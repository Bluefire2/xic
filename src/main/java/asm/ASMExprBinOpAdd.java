package asm;

public class ASMExprBinOpAdd extends ASMExprBinOp {

    public ASMExprBinOpAdd(ASMExpr left, ASMExpr right){
        super(left, right);
    }

    @Override
    public String toString() {
        return getLeft().toString() + " + " + getRight().toString();
    }
}