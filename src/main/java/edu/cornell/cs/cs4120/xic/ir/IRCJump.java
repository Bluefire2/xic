package edu.cornell.cs.cs4120.xic.ir;

import com.google.common.primitives.Longs;
import edu.cornell.cs.cs4120.util.SExpPrinter;
import edu.cornell.cs.cs4120.xic.ir.visit.ASMTranslationVisitor;
import edu.cornell.cs.cs4120.xic.ir.visit.AggregateVisitor;
import edu.cornell.cs.cs4120.xic.ir.visit.CheckCanonicalIRVisitor;
import edu.cornell.cs.cs4120.xic.ir.visit.IRVisitor;
import kc875.asm.ASMInstr;

import java.util.List;

/**
 * An intermediate representation for a conditional transfer of control
 * CJUMP(expr, trueLabel, falseLabel)
 */
public class IRCJump extends IRStmt {
    private IRExpr cond;
    private String trueLabel, falseLabel;

    /**
     * Construct a CJUMP instruction with fall-through on false.
     * @param cond the condition for the jump
     * @param trueLabel the destination of the jump if {@code expr} evaluates
     *          to true
     */
    public IRCJump(IRExpr cond, String trueLabel) {
        this(cond, trueLabel, null);
    }

    /**
     *
     * @param cond the condition for the jump
     * @param trueLabel the destination of the jump if {@code expr} evaluates
     *          to true
     * @param falseLabel the destination of the jump if {@code expr} evaluates
     *          to false
     */
    public IRCJump(IRExpr cond, String trueLabel, String falseLabel) {
        this.cond = cond;
        this.trueLabel = trueLabel;
        this.falseLabel = falseLabel;
    }

    public IRExpr cond() {
        return cond;
    }

    public String trueLabel() {
        return trueLabel;
    }

    public String falseLabel() {
        return falseLabel;
    }

    public boolean hasFalseLabel() {
        return falseLabel != null;
    }

    @Override
    public String label() {
        return "CJUMP";
    }

    @Override
    public IRNode visitChildren(IRVisitor v) {
        IRExpr expr = (IRExpr) v.visit(this, this.cond);

        if (expr != this.cond)
            return v.nodeFactory().IRCJump(expr, trueLabel, falseLabel);

        return this;
    }

    @Override
    public <T> T aggregateChildren(AggregateVisitor<T> v) {
        T result = v.unit();
        result = v.bind(result, v.visit(cond));
        return result;
    }

    @Override
    public boolean isCanonical(CheckCanonicalIRVisitor v) {
        return !hasFalseLabel();
    }

    @Override
    public List<ASMInstr> accept(ASMTranslationVisitor v) {
        return v.visit(this);
    }

    @Override
    public void printSExp(SExpPrinter p) {
        p.startList();
        p.printAtom("CJUMP");
        cond.printSExp(p);
        p.printAtom(trueLabel);
        if (hasFalseLabel()) p.printAtom(falseLabel);
        p.endList();
    }

    @Override
    public boolean equals(Object node) {
        if (node instanceof IRCJump) {
            IRCJump ircJump = (IRCJump) node;
            return cond.equals(ircJump.cond);
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        String hs;
        if (hasFalseLabel()) {
            hs = "2" + Math.abs(cond.hashCode()) +
                    Math.abs(trueLabel.hashCode()) + Math.abs(falseLabel.hashCode());
        }
        else {
            hs = "2" + Math.abs(cond.hashCode()) + Math.abs(trueLabel.hashCode());
        }
        hs = hs.replace("-", "");
        Long hl;
        try {
            hl = Long.parseLong(hs);
        }
        catch (NumberFormatException e) {
            hl = Long.parseLong(hs.substring(0, Math.min(18, hs.length())));
        }
        return Longs.hashCode(hl);
    }
}
