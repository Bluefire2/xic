package ast;

import edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;
import java_cup.runtime.ComplexSymbolFactory;

public class AssignableExpr extends Assignable {
    private Expr e;

    public AssignableExpr(Expr e, ComplexSymbolFactory.Location location) {
        super(location);
        this.e = e;
    }

    public Expr getExpr() {
        return e;
    }

    @Override
    public void prettyPrint(CodeWriterSExpPrinter w) {
        e.prettyPrint(w);
    }

    @Override
    public void accept(VisitorAST visitor) {
        e.accept(visitor);
        visitor.visit(this);
    }
}
