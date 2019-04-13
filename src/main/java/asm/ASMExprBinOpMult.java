package asm;

public class ASMExprBinOpMult extends ASMExprBinOp {

    public ASMExprBinOpMult(ASMExpr left, ASMExpr right) {
        super(left, right);
    }

    @Override
    public String toString() {
        return getLeft().toString() + " * " + getRight().toString();
    }
}
