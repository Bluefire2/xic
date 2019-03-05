package ast;

import edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;
import edu.cornell.cs.cs4120.xic.ir.IRNode;
import java_cup.runtime.ComplexSymbolFactory;

public class ExprIndex extends Expr {
    private Expr array;
    private Expr index;

    public ExprIndex(Expr array, Expr index,
                     ComplexSymbolFactory.Location location) {
        super(location);
        this.array = array;
        this.index = index;
        this.e_type = ExprType.IndexExpr;
    }

    public Expr getArray() {
        return array;
    }

    public Expr getIndex() {
        return index;
    }

    public void prettyPrint(CodeWriterSExpPrinter w) {
        w.startList();
        w.printAtom("[]");
        array.prettyPrint(w);
        index.prettyPrint(w);
        w.endList();
    }

    @Override
    public void accept(VisitorTypeCheck visitor) {
        array.accept(visitor);
        index.accept(visitor);
        visitor.visit(this);
    }

    @Override
    public IRNode accept(VisitorTranslation visitor) {
        return visitor.visit(this);
    }
}
