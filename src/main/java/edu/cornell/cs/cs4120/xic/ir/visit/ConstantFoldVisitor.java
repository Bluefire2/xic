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
        return irnode.visitChildren(this);
    }

    public IRNode fold(IRCJump irnode) {
        irnode = (IRCJump) irnode;
        IRExpr cond = irnode.cond();
        if (cond instanceof IRConst) {
            long condval = ((IRConst) cond).value();
            if (condval == 1) return new IRJump(new IRName(irnode.trueLabel()));
            else if (condval == 0) {
                if (irnode.hasFalseLabel())
                    return new IRJump(new IRName(irnode.falseLabel()));
                else return new IRSeq();
            }
        }
        return irnode;
    }

    public IRNode fold(IRCompUnit irnode) {
        return irnode.visitChildren(this);
    }

    public IRNode fold(IRConst irnode) {
        return irnode;
    }

    public IRNode fold(IRESeq irnode) {
        return irnode.visitChildren(this);
    }

    public IRNode fold(IRExp irnode) {
        return irnode.visitChildren(this);
    }

    public IRNode fold(IRFuncDecl irnode) {
        return irnode.visitChildren(this);
    }

    public IRNode fold(IRJump irnode) {
        return irnode.visitChildren(this);
    }

    public IRNode fold(IRLabel irnode) {
        return irnode;
    }

    public IRNode fold(IRMem irnode) {
        return irnode.visitChildren(this);
    }

    public IRNode fold(IRMove irnode) {
        return irnode.visitChildren(this);
    }

    public IRNode fold(IRName irnode) {
        return irnode;
    }

    public IRNode fold(IRReturn irnode) {
        return irnode.visitChildren(this);
    }

    public IRNode fold(IRSeq irnode) {
        return irnode.visitChildren(this);
    }

    public IRNode fold(IRTemp irnode) {
        return irnode;
    }
}
