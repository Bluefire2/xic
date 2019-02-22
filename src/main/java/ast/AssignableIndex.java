package ast;

import edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;
import java_cup.runtime.Symbol;

public class AssignableIndex extends Assignable {
    private Expr index;

    public AssignableIndex(Expr index, Symbol token) {
        super(token);
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
