package kc875.asm;

import java.util.Set;

/**
 * Can be replaced with regs without adding new instructions.
 */
public abstract class ASMExprRegReplaceable extends ASMExpr {
    @Override
    public Set<ASMExprRegReplaceable> vars() {
        return Set.of(this);
    }
}
