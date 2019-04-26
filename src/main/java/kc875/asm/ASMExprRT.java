package kc875.asm;

import java.util.Set;

/**
 * Can be replaced with regs without adding new instructions.
 */
public abstract class ASMExprRT extends ASMExpr {
    @Override
    public Set<ASMExprRT> vars() {
        return Set.of(this);
    }
}
