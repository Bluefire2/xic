package asm;

public abstract class ASMExprBinOp extends ASMExpr {
    private ASMExpr left;
    private ASMExpr right;

    ASMExprBinOp(ASMExpr left, ASMExpr right) {
        if (left == null || left instanceof ASMExprMem
                || right == null || right instanceof ASMExprMem) {
            throw new IllegalAccessError("Illegal argument of ASM expr");
        }
        this.left = left;
        this.right = right;
    }

    public ASMExpr getLeft() {
        return left;
    }

    public ASMExpr getRight() {
        return right;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ASMExprBinOp) {
            ASMExprBinOp o = (ASMExprBinOp) obj;
            return this.left.equals(o.left) && this.right.equals(o.right);
        }
        return false;
    }
}
