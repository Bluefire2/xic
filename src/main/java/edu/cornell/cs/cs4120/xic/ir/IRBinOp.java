package edu.cornell.cs.cs4120.xic.ir;

import com.google.common.primitives.Longs;
import edu.cornell.cs.cs4120.util.SExpPrinter;
import edu.cornell.cs.cs4120.xic.ir.visit.ASMTranslationVisitor;
import edu.cornell.cs.cs4120.xic.ir.visit.AggregateVisitor;
import edu.cornell.cs.cs4120.xic.ir.visit.CheckConstFoldedIRVisitor;
import edu.cornell.cs.cs4120.xic.ir.visit.IRVisitor;
import kc875.asm.ASMExprRT;
import kc875.asm.ASMInstr;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

/**
 * An intermediate representation for a binary operation
 * OP(left, right)
 */
public class IRBinOp extends IRExpr_c {

    /**
     * Binary operators
     */
    public enum OpType {
        ADD, SUB, MUL, HMUL, DIV, MOD, AND, OR, XOR, LSHIFT, RSHIFT, ARSHIFT,
        EQ, NEQ, LT, GT, LEQ, GEQ
    }

    private OpType type;
    private IRExpr left, right;

    public IRBinOp(OpType type, IRExpr left, IRExpr right) {
        this.type = type;
        this.left = left;
        this.right = right;
    }

    public OpType opType() {
        return type;
    }

    public IRExpr left() {
        return left;
    }

    public IRExpr right() {
        return right;
    }

    @Override
    public String label() {
        return type.toString();
    }

    @Override
    public IRNode visitChildren(IRVisitor v) {
        IRExpr left = (IRExpr) v.visit(this, this.left);
        IRExpr right = (IRExpr) v.visit(this, this.right);

        if (left != this.left || right != this.right)
            return v.nodeFactory().IRBinOp(type, left, right);

        return this;
    }

    @Override
    public <T> T aggregateChildren(AggregateVisitor<T> v) {
        T result = v.unit();
        result = v.bind(result, v.visit(left));
        result = v.bind(result, v.visit(right));
        return result;
    }

    @Override
    public boolean isConstFolded(CheckConstFoldedIRVisitor v) {
        if (isConstant()) {
            switch (type) {
                case DIV:
                case MOD:
                    return right.constant() == 0;
                default:
                    return false;
            }
        }
        return true;
    }

    @Override
    public List<ASMInstr> accept(ASMTranslationVisitor v, ASMExprRT t) {
        return v.visit(this, t);
    }

    @Override
    public <T> T matchLow(Function<IRBinOp, T> a,
                          Function<IRCall, T> b,
                          Function<IRConst, T> c,
                          Function<IRMem, T> d,
                          Function<IRName, T> e,
                          Function<IRTemp, T> f) {
        return a.apply(this);
    }

    @Override
    public boolean isConstant() {
        return left.isConstant() && right.isConstant();
    }

    @Override
    public void printSExp(SExpPrinter p) {
        p.startList();
        p.printAtom(type.toString());
        left.printSExp(p);
        right.printSExp(p);
        p.endList();
    }

    @Override
    public boolean equals(Object node) {
        if (node instanceof IRBinOp) {
            IRBinOp binOp = (IRBinOp) node;
            return binOp.opType().equals(type)
                    && this.left.equals(binOp.left)
                    && this.right.equals(binOp.right);
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        int opHash = Arrays.asList(OpType.values()).indexOf(type);

        String hs = "0" + Math.abs(opHash) + "" + Math.abs(left.hashCode()) + "" + Math.abs(right.hashCode());
        hs = hs.replace("-", "");
        Long hl;
        try {
            hl = Long.parseLong(hs);
        } catch (NumberFormatException e) {
            hl = Long.parseLong(hs.substring(0, Math.min(18, hs.length())));
        }
        return Longs.hashCode(hl);
    }

}
