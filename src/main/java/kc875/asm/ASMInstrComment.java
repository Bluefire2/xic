package kc875.asm;

import kc875.asm.visit.ASMinstrBareVisitor;

import java.util.*;

public class ASMInstrComment extends ASMInstr {
    private String comment;

    public ASMInstrComment(String comment) {
        super(ASMOpCode.COMMENT);
        this.comment = comment;
    }

    public String getComment() {return comment;}

    @Override
    public String toString() {
        return "#"+ comment;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof ASMInstrComment &&
                Objects.equals(((ASMInstrComment) obj).getComment(), this.comment);
    }

    @Override
    public List<ASMInstr> accept(ASMinstrBareVisitor<List<ASMInstr>> v) {
        return new ArrayList<>();
    }

    @Override
    public boolean destHasNewDef() {
        return false;
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
        return false;
    }
}
