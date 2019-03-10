package edu.cornell.cs.cs4120.xic.ir.visit;

import edu.cornell.cs.cs4120.xic.ir.*;

public class ConstantFoldVisitor extends IRVisitor {

    public ConstantFoldVisitor(IRNodeFactory inf) {
        super(inf);
    }


    @Override
    public IRNode leave(IRNode parent, IRNode n, IRNode n_, IRVisitor v_) {
        if (n_ instanceof IRBinOp) return fold((IRBinOp) n_);
        if (n_ instanceof IRCall) return fold((IRCall) n_);
        if (n_ instanceof IRCJump) return fold((IRCJump) n_);
        if (n_ instanceof IRCompUnit) return fold((IRCompUnit) n_);
        if (n_ instanceof IRConst) return fold((IRConst) n_);
        if (n_ instanceof IRESeq) return fold((IRESeq) n_);
        if (n_ instanceof IRExp) return fold((IRExp) n_);
        if (n_ instanceof IRFuncDecl) return fold((IRFuncDecl) n_);
        if (n_ instanceof IRJump) return fold((IRJump) n_);
        if (n_ instanceof IRLabel) return fold((IRLabel) n_);
        if (n_ instanceof IRMem) return fold((IRMem) n_);
        if (n_ instanceof IRMove) return fold((IRMove) n_);
        if (n_ instanceof IRName) return fold((IRName) n_);
        if (n_ instanceof IRReturn) return fold((IRReturn) n_);
        if (n_ instanceof IRSeq) return fold((IRSeq) n_);
        if (n_ instanceof IRTemp) return fold((IRTemp) n_);
        else return null; //Illegal state
    }

    public IRNode fold(IRBinOp irnode) {
        //TODO
        return irnode;
    }

    public IRNode fold(IRCall irnode) {
        //TODO
        return irnode;
    }

    public IRNode fold(IRCJump irnode) {
        //TODO
        return irnode;
    }

    public IRNode fold(IRCompUnit irnode) {
        //TODO
        return irnode;
    }

    public IRNode fold(IRConst irnode) {
        //TODO
        return irnode;
    }

    public IRNode fold(IRESeq irnode) {
        //TODO
        return irnode;
    }

    public IRNode fold(IRExp irnode) {
        //TODO
        return irnode;
    }

    public IRNode fold(IRFuncDecl irnode) {
        //TODO
        return irnode;
    }

    public IRNode fold(IRJump irnode) {
        //TODO
        return irnode;
    }

    public IRNode fold(IRLabel irnode) {
        //TODO
        return irnode;
    }

    public IRNode fold(IRMem irnode) {
        //TODO
        return irnode;
    }

    public IRNode fold(IRMove irnode) {
        //TODO
        return irnode;
    }

    public IRNode fold(IRName irnode) {
        //TODO
        return irnode;
    }

    public IRNode fold(IRReturn irnode) {
        //TODO
        return irnode;
    }

    public IRNode fold(IRSeq irnode) {
        //TODO
        return irnode;
    }

    public IRNode fold(IRTemp irnode) {
        //TODO
        return irnode;
    }
}
