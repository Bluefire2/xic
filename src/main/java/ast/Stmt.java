package ast;

import edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;
import java_cup.runtime.ComplexSymbolFactory;
import symboltable.SymbolTable;

import java.util.List;

public abstract class Stmt extends ASTNode implements Printable {
    StmtType s_type;
    SymbolTable symTable = null;
    TypeR typeCheckType = null;

    public Stmt(ComplexSymbolFactory.Location location) {
        super(location);
    }

    public StmtType getS_type() {
        return s_type;
    }

    public SymbolTable getSymTable() {
        return symTable;
    }

    public void setSymTable(SymbolTable symTable) {
        this.symTable = symTable;
    }

    public TypeR getTypeCheckType() {
        return typeCheckType;
    }

    public void setTypeCheckType(TypeR typeCheckType) {
        this.typeCheckType = typeCheckType;
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
