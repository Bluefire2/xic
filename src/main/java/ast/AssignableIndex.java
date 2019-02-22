package ast;

import edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;

public class AssignableIndex extends Assignable {
    private Expr index; // has to be an ID!

    public AssignableIndex(Expr index, int left, int right) {
        super(left, right);
        this.index = index;
    }

    public Expr getIndex() {
        return index;
    }

    @Override
    public void prettyPrint(CodeWriterSExpPrinter w) {
        index.prettyPrint(w);
    }

    @Override
    public void accept(VisitorAST visitor) throws ASTException {
        index.accept(visitor);
        visitor.visit(this);
    }
}
