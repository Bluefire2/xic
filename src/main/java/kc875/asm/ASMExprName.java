package kc875.asm;

import java.util.HashSet;
import java.util.Set;

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

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ASMExprName) {
            return this.name.equals(((ASMExprName) obj).name);
        }
        return false;
    }

    @Override
    public Set<ASMExprRegReplaceable> vars() {
        return new HashSet<>();
    }
}
