package asm;

import asm.visit.ASMinstrBareVisitor;
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
    public List<ASMInstr> accept(ASMinstrBareVisitor<List<ASMInstr>> v) {
        return new ArrayList<>();
    }
}