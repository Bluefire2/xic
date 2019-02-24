package ast;

import edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;
import java_cup.runtime.ComplexSymbolFactory;

public class AssignableId extends Assignable {
    private ExprId id;

    public AssignableId(ExprId id, ComplexSymbolFactory.Location location) {
        super(location);
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
    public void accept(VisitorAST visitor) {
        id.accept(visitor);
        visitor.visit(this);
    }
}
