package asm;

public class ASMExprBinOpMult extends ASMExprBinOp {

    public ASMExprBinOpMult(ASMExpr left, ASMExpr right) {
        super(left, right);
        if (left == null || left instanceof ASMExprMem
                || right == null || right instanceof ASMExprMem) {
            throw new IllegalAccessError("Illegal argument of ASM expr");
        }
    }

    @Override
    public String toString() {
        return getLeft().toString() + " * " + getRight().toString();
    }
}
