package asm;

public class ASMExprTemp extends ASMExpr {
    private String name;

    public ASMExprTemp(String name) {
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
