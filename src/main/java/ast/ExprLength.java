package ast;

import edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;
import java_cup.runtime.ComplexSymbolFactory;

public class ExprLength extends Expr {
    private Expr array;

    public ExprLength(Expr array, ComplexSymbolFactory.Location location) {
        super(location);
        this.array = array;
        this.e_type = ExprType.LengthExpr;
    }

    public Expr getArray() {
        return array;
    }

    public void prettyPrint(CodeWriterSExpPrinter w) {
        w.startList();
        w.printAtom("length");
        array.prettyPrint(w);
        w.endList();
    }

    @Override
    public void accept(VisitorAST visitor) {
        array.accept(visitor);
        visitor.visit(this);
    }
}
