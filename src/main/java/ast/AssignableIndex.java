package ast;

import edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;
import edu.cornell.cs.cs4120.xic.ir.IRNode;
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
    public void accept(VisitorTypeCheck visitor) {
        index.accept(visitor);
        visitor.visit(this);
    }

    @Override
    public IRNode accept(VisitorTranslation visitor) {
        return visitor.visit(this);
    }
}
