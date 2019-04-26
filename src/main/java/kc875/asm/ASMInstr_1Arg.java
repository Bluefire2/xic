package kc875.asm;

import kc875.asm.visit.ASMinstrBareVisitor;

import java.util.List;

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
    public boolean hasNewDef() {
        if (!(arg instanceof ASMExprRT)) {
            return false;
        }
        // arg is reg/temp
        switch (this.getOpCode()) {
            case IMUL:
            case IDIV:
            case INC:
            case DEC:
            case NOT:
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
