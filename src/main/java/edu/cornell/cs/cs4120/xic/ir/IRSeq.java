package edu.cornell.cs.cs4120.xic.ir;

import com.google.common.primitives.Longs;
import edu.cornell.cs.cs4120.util.SExpPrinter;
import edu.cornell.cs.cs4120.xic.ir.visit.ASMTranslationVisitor;
import edu.cornell.cs.cs4120.xic.ir.visit.AggregateVisitor;
import edu.cornell.cs.cs4120.xic.ir.visit.CheckCanonicalIRVisitor;
import edu.cornell.cs.cs4120.xic.ir.visit.IRVisitor;
import kc875.asm.ASMInstr;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * An intermediate representation for a sequence of statements
 * SEQ(s1,...,sn)
 */
public class IRSeq extends IRStmt {
    private List<IRStmt> stmts;
    private boolean replaceParent;

    /**
     * @param stmts the statements
     */
    public IRSeq(IRStmt... stmts) {
        this(Arrays.asList(stmts));
    }

    private <T> List<T> filterNulls(List<T> list) {
        return list
                .stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }
    /**
     * Create a SEQ from a list of statements.
     * The list should not be modified subsequently.
     * @param stmts the sequence of statements
     */
    public IRSeq(List<IRStmt> stmts) {
        // filter out nulls
        this.stmts = filterNulls(stmts);
        this.replaceParent = false;
    }

    public IRSeq(List<IRStmt> stmts, boolean replaceParent) {
        this.stmts = filterNulls(stmts);;
        this.replaceParent = replaceParent;
    }

    public List<IRStmt> stmts() {
        return stmts;
    }

    @Override
    public String label() {
        return "SEQ";
    }

    @Override
    public IRNode visitChildren(IRVisitor v) {
        boolean modified = false;

        List<IRStmt> results = new ArrayList<>(stmts.size());
        for (IRStmt stmt : stmts) {
            IRStmt newStmt = (IRStmt) v.visit(this, stmt);
            if (newStmt != stmt) modified = true;
            results.add(newStmt);
        }

        if (modified) return v.nodeFactory().IRSeq(results);

        return this;
    }

    @Override
    public <T> T aggregateChildren(AggregateVisitor<T> v) {
        T result = v.unit();
        for (IRStmt stmt : stmts)
            result = v.bind(result, v.visit(stmt));
        return result;
    }

    @Override
    public CheckCanonicalIRVisitor checkCanonicalEnter(
            CheckCanonicalIRVisitor v) {
        return v.enterSeq();
    }

    @Override
    public boolean isCanonical(CheckCanonicalIRVisitor v) {
        return !v.inSeq();
    }

    @Override
    public List<ASMInstr> accept(ASMTranslationVisitor v) {
        return v.visit(this);
    }

    @Override
    public void printSExp(SExpPrinter p) {
        p.startUnifiedList();
        p.printAtom("SEQ");
        for (IRStmt stmt : stmts)
            stmt.printSExp(p);
        p.endList();
    }

    @Override
    public boolean equals(Object node) {
        if (node instanceof IRSeq) {
            IRSeq irSeq = (IRSeq) node;
            return stmts.equals(irSeq.stmts);
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        String stmtCode = "";
        for (IRStmt s : stmts) {
            stmtCode += s.hashCode();
        }
        String hs = "12" + stmtCode;
        Long hl;
        try {
            hl = Long.parseLong(hs);
        }
        catch (NumberFormatException e) {
            hl = Long.parseLong(hs.substring(0,18));
        }
        return Longs.hashCode(hl);
    }

    public boolean isReplaceParent() {
        return replaceParent;
    }

    public void setReplaceParent(boolean replaceParent) {
        this.replaceParent = replaceParent;
    }
}
