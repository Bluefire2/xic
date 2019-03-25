package asm;

public class ASMExprLabel extends ASMExpr {
    private String name;

    public ASMExprLabel(String name) {
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
