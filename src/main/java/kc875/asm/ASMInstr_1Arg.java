package kc875.asm;

import kc875.asm.visit.ASMinstrBareVisitor;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ASMInstr_1Arg extends ASMInstr {
    private ASMExpr arg;

    public ASMInstr_1Arg(ASMOpCode opCode, ASMExpr arg) {
        super(opCode);
        this.arg = arg;
    }

    @Override
    public String toString() {
        return INDENT_TAB + formatOpCode() + " " + arg.toString();
    }

    @Override
    public List<ASMInstr> accept(ASMinstrBareVisitor<List<ASMInstr>> v) {
        return v.visit(this);
    }

    public ASMExpr getArg() {
        return arg;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ASMInstr_1Arg) {
            ASMInstr_1Arg o = (ASMInstr_1Arg) obj;
            return this.getOpCode().equals(o.getOpCode())
                    && this.arg.equals(o.arg);
        }
        return false;
    }

    @Override
    public boolean destHasNewDef() {
        if (!(arg instanceof ASMExprRT)) {
            return false;
        }
        // arg is reg/temp
        switch (this.getOpCode()) {
            case POP:
            case SETE:
            case SETNE:
            case SETG:
            case SETGE:
            case SETL:
            case SETLE:
                return true;
            default:
                return false;
        }
    }

    @Override
    public Set<ASMExprReg> implicitRegs() {
        Set<ASMExprReg> s = new HashSet<>();
        switch (getOpCode()) {
            case IMUL:
            case IDIV:
                s.add(new ASMExprReg("rax"));
                s.add(new ASMExprReg("rdx"));
                break;
            case CALL:
                // all caller save regs defined
                s.add(new ASMExprReg("rax"));
                s.add(new ASMExprReg("rdx"));
                s.add(new ASMExprReg("rcx"));
                s.add(new ASMExprReg("rdi"));
                s.add(new ASMExprReg("rsi"));
                s.add(new ASMExprReg("r8"));
                s.add(new ASMExprReg("r9"));
                s.add(new ASMExprReg("r10"));
                s.add(new ASMExprReg("r11"));
                break;
            default:
                break;
        }
        return s;
    }

    @Override
    public boolean destIsDefButNoUse() {
        if (!(arg instanceof ASMExprRT)) {
            return false;
        }
        // arg is reg/temp
        switch (this.getOpCode()) {
            case POP:
            case SETE:
            case SETNE:
            case SETG:
            case SETGE:
            case SETL:
            case SETLE:
                return true;
            default:
                return false;
        }
    }
}
