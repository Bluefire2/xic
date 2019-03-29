package asm;

public class ASMExprTemp extends ASMExpr {
    private String name;
    private boolean special;

    public ASMExprTemp(String name) {
        this.name = name;
        this.special = false;
    }

    public ASMExprTemp(String name, boolean special) {
        this.name = name;
        this.special = special;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }
}
