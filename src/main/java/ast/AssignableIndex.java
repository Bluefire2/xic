package ast;

import edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;
import java_cup.runtime.ComplexSymbolFactory;

public class AssignableIndex extends Assignable {
    private Expr index;

    public AssignableIndex(Expr index, ComplexSymbolFactory.Location location) {
        super(location);
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
    public void accept(VisitorAST visitor) {
        index.accept(visitor);
        visitor.visit(this);
    }
}
