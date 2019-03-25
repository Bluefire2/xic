package asm;

public class ASMExprReg extends ASMExpr {
    private String name;

    public ASMExprReg(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }
}
