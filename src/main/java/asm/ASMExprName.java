package asm;

/**
 * Wrapper for the name of the label. Used for control flow (jumping etc.)
 */
public class ASMExprName extends ASMExpr {
    private String name;

    public ASMExprName(String name) {
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
