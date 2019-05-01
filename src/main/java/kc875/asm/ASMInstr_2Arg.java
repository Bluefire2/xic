package kc875.asm;

import kc875.asm.visit.ASMinstrBareVisitor;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ASMInstr_2Arg extends ASMInstr {
    private ASMExpr dest;
    private ASMExpr src;

    public ASMInstr_2Arg(ASMOpCode opCode, ASMExpr dest, ASMExpr src) {
        super(opCode);
        this.dest = dest;
        this.src = src;
    }

    @Override
    public String toString() {
        return INDENT_TAB + formatOpCode() + " " +
                dest.toString() + ", " + src.toString();
    }

    @Override
    public List<ASMInstr> accept(ASMinstrBareVisitor<List<ASMInstr>> v) {
        return v.visit(this);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ASMInstr_2Arg) {
            ASMInstr_2Arg o = (ASMInstr_2Arg) obj;
            return this.getOpCode().equals(o.getOpCode())
                    && this.dest.equals(o.dest)
                    && this.src.equals(o.src);
        }
        return false;
    }

    public ASMExpr getDest() {
        return dest;
    }

    public ASMExpr getSrc() {
        return src;
    }

    @Override
    public boolean destHasNewDef() {
        if (!(dest instanceof ASMExprRT)) {
            return false;
        }
        // dest is reg/temp
        switch (getOpCode()) {
            case ADD:
            case SUB:
            case IMUL:
            case IDIV:
            case AND:
            case OR:
            case XOR:
            case SHR:
            case SHL:
            case SAR:
            case MOV:
            case MOVZX:
                return true;
            default:
                return false;
        }
    }

    @Override
    public Set<ASMExprReg> implicitDefRegs() {
        return new HashSet<>();
    }

    @Override
    public Set<ASMExprReg> implicitUsedRegs() {
        return new HashSet<>();
    }

    @Override
    public boolean destIsDefButNoUse() {
        if (!(dest instanceof ASMExprRT)) {
            return false;
        }
        // dest is reg/temp
        switch (this.getOpCode()) {
            case MOV:
            case MOVZX:
                return true;
            default:
                return false;
        }
    }
}
