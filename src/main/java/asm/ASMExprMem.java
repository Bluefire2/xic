package asm;

public class ASMExprMem extends ASMExpr {
    private ASMExpr addr;

    public ASMExprMem(ASMExpr addr) {
        this.addr = addr;
    }

    public ASMExpr getAddr() {
        return addr;
    }

    @Override
    public String toString() {
        return "[" + addr.toString() + "]";
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ASMExprMem) {
            return this.addr.equals(((ASMExprMem) obj).addr);
        }
        return false;
    }
}
