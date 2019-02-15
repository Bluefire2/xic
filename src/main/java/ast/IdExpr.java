package ast;

import edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;

public class IdExpr extends Expr {
    private String name;

    IdExpr(String name) {
        this.name = name;
        this.e_type = ExprType.IdExpr;
    }

    public String getName() {
        return name;
    }

    public void prettyPrint(CodeWriterSExpPrinter w) {
        w.printAtom(name);
    }
}
