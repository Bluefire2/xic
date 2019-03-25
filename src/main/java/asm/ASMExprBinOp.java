package asm;

public abstract class ASMExprBinOp extends ASMExpr {
    private ASMExpr left;
    private ASMExpr right;

    ASMExprBinOp(ASMExpr left, ASMExpr right) {
        this.left = left;
        this.right = right;
    }

    public ASMExpr getLeft() {
        return left;
    }

    public ASMExpr getRight() {
        return right;
    }
}
