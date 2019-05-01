package edu.cornell.cs.cs4120.xic.ir;

import com.google.common.primitives.Longs;
import edu.cornell.cs.cs4120.util.SExpPrinter;
import edu.cornell.cs.cs4120.xic.ir.visit.ASMTranslationVisitor;
import edu.cornell.cs.cs4120.xic.ir.visit.AggregateVisitor;
import edu.cornell.cs.cs4120.xic.ir.visit.IRVisitor;
import kc875.asm.ASMInstr;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/** RETURN statement */
public class IRReturn extends IRStmt {
    protected List<IRExpr> rets;

    /**
     * @param rets values to return
     */
    public IRReturn(IRExpr... rets) {
        this(Arrays.asList(rets));
    }

    /**
     * @param rets values to return
     */
    public IRReturn(List<IRExpr> rets) {
        this.rets = rets;
    }

    public List<IRExpr> rets() {
        return rets;
    }

    @Override
    public String label() {
        return "RETURN";
    }

    @Override
    public IRNode visitChildren(IRVisitor v) {
        boolean modified = false;

        List<IRExpr> results = new ArrayList<>(rets.size());

        for (IRExpr ret : rets) {
            IRExpr newExpr = (IRExpr) v.visit(this, ret);
            if (newExpr != ret) modified = true;
            results.add(newExpr);
        }

        if (modified) return v.nodeFactory().IRReturn(results);

        return this;
    }

    @Override
    public <T> T aggregateChildren(AggregateVisitor<T> v) {
        T result = v.unit();
        for (IRExpr ret : rets)
            result = v.bind(result, v.visit(ret));
        return result;
    }

    @Override
    public List<ASMInstr> accept(ASMTranslationVisitor v) {
        return v.visit(this);
    }

    @Override
    public void printSExp(SExpPrinter p) {
        p.startList();
        p.printAtom("RETURN");
        for (IRExpr ret : rets)
            ret.printSExp(p);
        p.endList();
    }

    @Override
    public boolean equals(Object node) {
        if (node instanceof IRReturn) {
            return rets.equals(((IRReturn) node).rets);
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        String retCode = "";
        for (IRExpr r : rets) {
            retCode += Math.abs(r.hashCode());
        }
        String hs = "11" + retCode;
        Long hl;
        try {
            hl = Long.parseLong(hs);
        }
        catch (NumberFormatException e) {
            hl = Long.parseLong(hs.substring(0,18));
        }
        return Longs.hashCode(hl);
    }
}
