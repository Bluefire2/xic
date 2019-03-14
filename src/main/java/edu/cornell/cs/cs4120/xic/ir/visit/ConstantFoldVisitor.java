package edu.cornell.cs.cs4120.xic.ir.visit;

import edu.cornell.cs.cs4120.util.InternalCompilerError;
import edu.cornell.cs.cs4120.xic.ir.*;

import java.math.BigInteger;

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

    public IRNode strengthReduce(IRBinOp.OpType op, IRName n, IRConst c, IRNode node, boolean constIsRight) {
        //TODO:
        return node;
    }

    public IRNode fold(IRBinOp irnode) {
        //TODO: reassociation, strength reduction, other algebraic identities(?)
        irnode = (IRBinOp) irnode.visitChildren(this);
        IRExpr l = irnode.left();
        IRExpr r = irnode.right();
        IRBinOp.OpType op = irnode.opType();
        if (l instanceof IRConst && r instanceof IRConst) {
            long lval = ((IRConst) l).value();
            long rval = ((IRConst) r).value();
            switch (op) {
                case ADD:
                    return new IRConst(lval + rval);
                case SUB:
                    return new IRConst(lval - rval);
                case MUL:
                    return new IRConst(lval * rval);
                case HMUL:
                    return new IRConst(BigInteger.valueOf(lval)
                            .multiply(BigInteger.valueOf(rval))
                            .shiftRight(64)
                            .longValue());
                case DIV:
                    if (rval == 0L) {
                        return irnode;
                    }
                    return new IRConst(lval / rval);
                case MOD:
                    if (rval == 0L) {
                        return irnode;
                    }
                    return new IRConst(lval % rval);
                case EQ:
                    return new IRConst((lval == rval) ? 1 : 0);
                case NEQ:
                    return new IRConst((lval != rval) ? 1 : 0);
                case GT:
                    return new IRConst((lval > rval) ? 1 : 0);
                case LT:
                    return new IRConst((lval < rval) ? 1 : 0);
                case GEQ:
                    return new IRConst((lval >= rval) ? 1 : 0);
                case LEQ:
                    return new IRConst((lval <= rval) ? 1 : 0);
                case AND:
                    return new IRConst((lval == 1 && rval == 1) ? 1 : 0);
                case OR:
                    return new IRConst((lval == 1 || rval == 1) ? 1 : 0);
                default:
                    throw new InternalCompilerError("Invalid binary operation");
            }
        } else if (l instanceof IRConst && r instanceof IRName) {
            return foldNameAndConstBinOp(irnode, (IRConst) l, (IRName) r, op, false);
        } else if (r instanceof IRConst && l instanceof IRName) {
            return foldNameAndConstBinOp(irnode, (IRConst) r, (IRName) l, op, true);
        }
        return irnode;
    }

    private IRNode foldNameAndConstBinOp(IRBinOp irnode, IRConst c, IRName e, IRBinOp.OpType opType, boolean constIsRight) {
        long value = c.value();
        switch (opType) {
            case ADD:
                // if we have x + 0 or 0 + x, that's just x
                if (value == 0) return e; else return irnode;
            case SUB:
                // if we have x - 0, that's just x
                if (value == 0 && constIsRight) return e; else return irnode;
            case MUL:
                // if we have x * 1 or 1 * x, that's just x
                if (value == 1) return e;
                // if we have x * 0 or 0 * x, that's just 0
                else if (value == 0) return new IRConst(0);
                else return strengthReduce(opType, e, c, irnode, constIsRight);
            case DIV:
                // if we have x / 1, that's just x
                if (value == 1 && constIsRight) return e;
                else return strengthReduce(opType, e, c, irnode, constIsRight);
            case AND:
                if (value == 1) return e;
                else if (value == 0) return new IRConst(0);
                else return irnode;
            case OR:
                if (value == 0) return e;
                else if (value == 1) return new IRConst(1);
                else return irnode;
             default:
                 return irnode;
        }
    }

    public IRNode fold(IRCall irnode) {
        return irnode.visitChildren(this);
    }

    public IRNode fold(IRCJump irnode) {
        irnode = (IRCJump) irnode.visitChildren(this);
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
