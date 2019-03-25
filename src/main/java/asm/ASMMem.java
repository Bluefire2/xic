package asm;

public class ASMMem {
    ASMExpr addr;

    ASMMem(ASMExpr addr) {
        this.addr = addr;
    }

    public ASMExpr getAddr() {
        return addr;
    }

    @Override
    public String toString() {
        return "[ " + addr.toString() + "]";
    }
}
