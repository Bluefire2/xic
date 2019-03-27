package edu.cornell.cs.cs4120.xic.ir.visit;

import asm.*;
import edu.cornell.cs.cs4120.xic.ir.*;

import java.util.ArrayList;
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
        List<ASMInstr> instrs = new ArrayList<>();
        String fname = node.name();

        //Prologue
        instrs.add(new ASMInstrOneArg(ASMOpCode.PUSH, new ASMExprReg("ebp")));
        instrs.add(new ASMInstrMove(ASMOpCode.MOV, new ASMExprReg("ebp"),
                new ASMExprReg("esp")));
        //set up stack frame for local vars?

        //Body
        //Args passed in rdi,rsi,rdx,rcx,r8,r9, (stack in reverse order)
        //If rbx,rbp, r12, r13, r14, r15 used, restore before returning
        //First return in rax, second in rdx

        //Epilogue
        instrs.add(new ASMInstrMove(ASMOpCode.MOV, new ASMExprReg("esp"),
                new ASMExprReg("ebp")));
        instrs.add(new ASMInstrOneArg(ASMOpCode.POP, new ASMExprReg("ebp")));
        instrs.add(new ASMInstrNoArgs(ASMOpCode.RET));

        return instrs;
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
