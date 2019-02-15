package ast;

import edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;

public class IdAssignable extends Assignable {
    private Expr id;

    IdAssignable(Expr id) {
        this.id = id;
    }

    public Expr getId() {
        return id;
    }

    @Override
    public void prettyPrint(CodeWriterSExpPrinter w) {
        id.prettyPrint(w);
    }
}
