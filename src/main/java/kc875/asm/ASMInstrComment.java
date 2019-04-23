package kc875.asm;

import kc875.asm.visit.ASMinstrBareVisitor;

import java.util.ArrayList;
import java.util.List;

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
                ((ASMInstrComment) obj).getComment() == this.comment;
    }

    @Override
    public List<ASMInstr> accept(ASMinstrBareVisitor<List<ASMInstr>> v) {
        return new ArrayList<>();
    }

    @Override
    public boolean hasNewDef() {
        return false;
    }
}
