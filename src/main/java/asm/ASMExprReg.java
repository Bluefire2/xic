package asm;

public class ASMExprReg extends ASMExpr {
    public enum x86_64Reg {
        rax,
        rbx,
        rcx,
        rdx,
        rsi,
        rdi,
        rbp,
        rsp,
        r8,
        r9,
        r10,
        r11,
        r12,
        r13,
        r14,
        r15,
        rip;
    }

    private x86_64Reg reg;

    public ASMExprReg(x86_64Reg reg) {
        this.reg = reg;
    }

    public x86_64Reg getReg() {
        return reg;
    }

    @Override
    public String toString() {
        return reg.toString();
    }
}
