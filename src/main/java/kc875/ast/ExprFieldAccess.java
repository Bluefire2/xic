package kc875.ast;

import edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;
import edu.cornell.cs.cs4120.xic.ir.IRNode;
import java_cup.runtime.ComplexSymbolFactory;
import kc875.ast.visit.IRTranslationVisitor;
import kc875.ast.visit.TypeCheckVisitor;

public class ExprFieldAccess extends Expr {
    private Expr obj;
    private ExprId field;

    public ExprFieldAccess(Expr obj, ExprId field,
                           ComplexSymbolFactory.Location location) {
        super(location);
        this.obj = obj;
        this.field = field;
    }

    public Expr getObj() {
        return obj;
    }

    public ExprId getField() {
        return field;
    }

    @Override
    public void accept(TypeCheckVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public IRNode accept(IRTranslationVisitor visitor) {
        return visitor.visit(this);
    }

    @Override
    public void prettyPrint(CodeWriterSExpPrinter w) {
        w.startList();
        w.printAtom(".");
        obj.prettyPrint(w);
        field.prettyPrint(w);
        w.endList();
    }
}
