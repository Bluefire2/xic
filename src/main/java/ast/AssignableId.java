package ast;

import edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;
import java_cup.runtime.Symbol;

public class AssignableId extends Assignable {
    private ExprId id;

    public AssignableId(ExprId id, Symbol token) {
        super(token);
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
    public void accept(VisitorAST visitor) throws ASTException {
        // TODO
    }
}
