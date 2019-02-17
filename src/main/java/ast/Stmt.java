package ast;

import edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;
import polyglot.util.Pair;
import symboltable.SymbolTable;

public abstract class Stmt implements Printable, TypeCheckable {
    StmtType s_type;
    SymbolTable ctx = null;
    StmtFallthrough ret = null;

    public StmtType getS_type() {
        return s_type;
    }

    public void printPair(Pair<String, Type> p, CodeWriterSExpPrinter w){
        if (!(p.part2() instanceof UnitType)){
            w.startList();
            w.printAtom(p.part1());
            p.part2().prettyPrint(w);
            w.endList();
        } else {
            w.printAtom("_");
        }
    }

    public SymbolTable getCtx() {
        return ctx;
    }

    public void setCtx(SymbolTable ctx) {
        this.ctx = ctx;
    }

    public StmtFallthrough getRet() {
        return ret;
    }

    public void setRet(StmtFallthrough ret) {
        this.ret = ret;
    }
}
