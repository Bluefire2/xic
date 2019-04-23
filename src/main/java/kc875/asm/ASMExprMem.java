package kc875.asm;

import java.util.Set;

public class ASMExprMem extends ASMExpr {
    private ASMExpr addr;

    public ASMExprMem(ASMExpr addr) {
        if (addr == null || addr instanceof ASMExprMem){
            throw new IllegalAccessError("illegal mem address");
        }
        this.addr = addr;
    }

    public ASMExpr getAddr() {
        return addr;
    }

    @Override
    public String toString() {
        return "QWORD PTR [" + addr.toString() + "]";
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ASMExprMem) {
            return this.addr.equals(((ASMExprMem) obj).addr);
        }
        return false;
    }

    @Override
    public Set<ASMExprRegReplaceable> vars() {
        return addr.vars();
    }
}
