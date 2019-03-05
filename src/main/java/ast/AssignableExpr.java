package ast;

import edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;
import edu.cornell.cs.cs4120.xic.ir.IRNode;
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
    public void accept(VisitorTypeCheck visitor) {
        e.accept(visitor);
        visitor.visit(this);
    }

    @Override
    public IRNode accept(VisitorTranslation visitor) {
        return visitor.visit(this);
    }
}
