package kc875.ast;

import edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;
import edu.cornell.cs.cs4120.xic.ir.IRNode;
import java_cup.runtime.ComplexSymbolFactory;
import kc875.ast.visit.IRTranslationVisitor;
import kc875.ast.visit.TypeCheckVisitor;

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
    public void accept(TypeCheckVisitor visitor) {
        array.accept(visitor);
        index.accept(visitor);
        visitor.visit(this);
    }

    @Override
    public IRNode accept(IRTranslationVisitor visitor) {
        return visitor.visit(this);
    }
}
