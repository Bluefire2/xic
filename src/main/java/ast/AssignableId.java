package ast;

import edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;

public class AssignableId extends Assignable {
    private ExprId id;

    public AssignableId(ExprId id, int left, int right) {
        super(left, right);
        this.id = id;
    }

    public ExprId getId() {
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
