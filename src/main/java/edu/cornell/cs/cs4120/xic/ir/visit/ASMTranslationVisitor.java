package edu.cornell.cs.cs4120.xic.ir.visit;

import asm.ASMInstr;
import edu.cornell.cs.cs4120.xic.ir.*;

import java.util.List;

public class ASMTranslationVisitor implements IRBareVisitor<List<ASMInstr>> {

    public ASMTranslationVisitor() {}

    public List<ASMInstr> visit(IRBinOp node) {
        // Example code for tiling
        List<ASMInstr> left = node.left().accept(this);
        List<ASMInstr> right = node.right().accept(this);
        throw new IllegalAccessError();
    }

    public List<ASMInstr> visit(IRCall node) {
        throw new IllegalAccessError();
    }

    public List<ASMInstr> visit(IRCJump node) {
        throw new IllegalAccessError();
    }

    public List<ASMInstr> visit(IRCompUnit node) {
        throw new IllegalAccessError();
    }

    public List<ASMInstr> visit(IRConst node) {
        throw new IllegalAccessError();
    }

    public List<ASMInstr> visit(IRExp node) {
        throw new IllegalAccessError();
    }

    public List<ASMInstr> visit(IRFuncDecl node) {
        throw new IllegalAccessError();
    }

    public List<ASMInstr> visit(IRJump node) {
        throw new IllegalAccessError();
    }

    public List<ASMInstr> visit(IRLabel node) {
        throw new IllegalAccessError();
    }

    public List<ASMInstr> visit(IRMem node) {
        throw new IllegalAccessError();
    }

    public List<ASMInstr> visit(IRMove node) {
        throw new IllegalAccessError();
    }

    public List<ASMInstr> visit(IRName node) {
        throw new IllegalAccessError();
    }

    public List<ASMInstr> visit(IRReturn node) {
        throw new IllegalAccessError();
    }

    public List<ASMInstr> visit(IRSeq node) {
        throw new IllegalAccessError();
    }

    public List<ASMInstr> visit(IRTemp node) {
        throw new IllegalAccessError();
    }
}
