package kc875.asm;

import java.util.HashSet;
import java.util.Set;

public class ASMExprConst extends ASMExpr implements ASMExprImm {
    private Long val;

    public ASMExprConst(long val) {
        this.val = val;
    }

    public Long getVal() {
        return val;
    }

    @Override
    public String toString() {
        return val.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ASMExprConst) {
            return this.val.longValue() == ((ASMExprConst) obj).val.longValue();
        }
        return false;
    }

    @Override
    public Set<ASMExprRT> vars() {
        return new HashSet<>();
    }
}
