package kc875.asm;

import kc875.asm.visit.ASMinstrBareVisitor;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ASMInstr_1Arg extends ASMInstr {
    private ASMExpr arg;

    public ASMInstr_1Arg(ASMOpCode opCode, ASMExpr arg) {
        super(opCode);
        if (arg instanceof ASMExprLabel) {
            throw new IllegalAccessError("labels cannot be used as operands!");
        }
        this.arg = arg;
    }

    @Override
    public String toString() {
        if (getOpCode() == ASMOpCode.RET)
            return INDENT_TAB + formatOpCode();
        else
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
            case INC:
            case DEC:
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
    public Set<ASMExprReg> implicitDefRegs() {
        Set<ASMExprReg> s = new HashSet<>();
        switch (getOpCode()) {
            case IMUL:
            case IDIV:
                s.add(new ASMExprReg("rax"));
                s.add(new ASMExprReg("rdx"));
                break;
            case SETE:
            case SETNE:
            case SETG:
            case SETGE:
            case SETL:
            case SETLE:
                s.add(new ASMExprReg("rax"));
            default:
                break;
        }
        return s;
    }

    @Override
    public Set<ASMExprReg> implicitUsedRegs() {
        Set<ASMExprReg> s = new HashSet<>();
        switch (getOpCode()) {
            case RET:
                if (!(arg instanceof ASMExprConst))
                    throw new IllegalAccessError("RET should have a const");
                long nRets = ((ASMExprConst) arg).getVal();
                if (nRets >= 2)
                    s.add(new ASMExprReg("rdx"));
                // always add rax (ABI spec)
                s.add(new ASMExprReg("rax"));
                return s;
            case IMUL:
            case IDIV:
                s.add(new ASMExprReg("rax"));
                s.add(new ASMExprReg("rdx"));
                return s;
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
