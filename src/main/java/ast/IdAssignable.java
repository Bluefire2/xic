package ast;

import edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;

public class IdAssignable extends Assignable {
    private IdExpr id;

    public IdAssignable(IdExpr id, int left, int right) {
        super(left, right);
        this.id = id;
    }

    public IdExpr getId() {
        return id;
    }

    @Override
    public void prettyPrint(CodeWriterSExpPrinter w) {
        id.prettyPrint(w);
    }

    @Override
    public void accept(VisitorAST visitor) throws SemanticErrorException {
        // TODO
    }
}
