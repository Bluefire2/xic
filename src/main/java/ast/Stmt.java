package ast;

import edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;
import polyglot.util.Pair;
import symboltable.SymbolTable;

import java.util.List;

public abstract class Stmt implements Printable, ASTNode {
    StmtType s_type;
    SymbolTable symTable = null;
    // TODO: again, do we need this; type checker visitor should take care
    TypeR ret = null;

    public StmtType getS_type() {
        return s_type;
    }

    public SymbolTable getSymTable() {
        return symTable;
    }

    public void setSymTable(SymbolTable symTable) {
        this.symTable = symTable;
    }

    public TypeR getRet() {
        return ret;
    }

    public void setRet(TypeR ret) {
        this.ret = ret;
    }

    <T extends Printable> void prettyPrintList(List<T> list,
                                               CodeWriterSExpPrinter w) {
        // Utility function for subclasses when pretty printing
        if (list.size() == 1) {
            list.get(0).prettyPrint(w);
        } else {
            w.startList();
            list.forEach(el -> el.prettyPrint(w));
            w.endList();
        }
    }
}
