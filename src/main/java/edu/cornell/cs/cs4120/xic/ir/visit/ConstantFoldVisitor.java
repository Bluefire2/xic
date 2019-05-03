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
        if (n_ instanceof IRCJump) return fold((IRCJump) n_);
        else return n_;
    }

    public IRNode strengthReduce(IRBinOp.OpType op, IRName n, IRConst c, IRNode node, boolean constIsRight) {
        //TODO:
        return node;
    }

    public IRNode fold(IRBinOp irnode) {
        //TODO: reassociation, strength reduction, other algebraic identities(?)
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

    /**
     * Fold a BinOp that consists of a const node and a name node.
     *
     * @param irnode The BinOp node.
     * @param c The const node.
     * @param e The name node.
     * @param opType The type of the BinOp.
     * @param constIsRight {@code true} if the const is on the right-hand side of the BinOp, {@code false} otherwise.
     * @return A folded BinOp which evaluates to the same value as the original BinOp.
     */
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
                // if we have 0 / x, that's just 0
                else if (value == 0 && !constIsRight) return new IRConst(0);
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

    public IRNode fold(IRCJump irnode) {
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
}
