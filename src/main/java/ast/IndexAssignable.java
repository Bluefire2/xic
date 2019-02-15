package ast;

import edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;

public class IndexAssignable extends Assignable {
    private Expr index; // has to be an ID!

    IndexAssignable(Expr index) {
        this.index = index;
    }

    public Expr getIndex() {
        return index;
    }

    @Override
    public void prettyPrint(CodeWriterSExpPrinter w) {
        index.prettyPrint(w);
    }
}