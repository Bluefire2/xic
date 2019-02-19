package ast;

import edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;

public class IdAssignable extends Assignable {
    private Expr id;

    public IdAssignable(Expr id, int left, int right) {
        super(left, right);
        this.id = id;
    }

    public Expr getId() {
        return id;
    }

    @Override
    public void prettyPrint(CodeWriterSExpPrinter w) {
        id.prettyPrint(w);
    }

    @Override
    public void accept(VisitorAST visitor) {
        // TODO
    }
}
