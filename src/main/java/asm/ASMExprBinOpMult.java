package asm;

public class ASMExprBinOpMult extends ASMExprBinOp {

    ASMExprBinOpMult(ASMExprReg left, ASMExprConst right) {
        super(left, right);
    }

    ASMExprBinOpMult(ASMExprConst left, ASMExprReg right) {
        super(left, right);
    }

    @Override
    public String toString() {
        return getLeft().toString() + " * " + getRight().toString();
    }
}
