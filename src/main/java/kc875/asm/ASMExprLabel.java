package kc875.asm;

import java.util.HashSet;
import java.util.Set;

public class ASMExprLabel extends ASMExpr implements ASMExprImm {
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

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ASMExprLabel) {
            return this.name.equals(((ASMExprLabel) obj).getName());
        }
        return false;
    }

    @Override
    public Set<ASMExprRT> vars() {
        return new HashSet<>();
    }
}