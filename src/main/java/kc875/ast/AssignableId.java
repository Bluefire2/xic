package kc875.ast;

import edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;
import edu.cornell.cs.cs4120.xic.ir.IRNode;
import java_cup.runtime.ComplexSymbolFactory;
import kc875.ast.visit.IRTranslationVisitor;
import kc875.ast.visit.TypeCheckVisitor;

public class AssignableId extends Assignable {
    private ExprId i;

    public AssignableId(ExprId i, ComplexSymbolFactory.Location location) {
        super(location);
        this.i = i;
    }

    public ExprId getExprId() {
        return i;
    }

    @Override
    public void prettyPrint(CodeWriterSExpPrinter w) {
        i.prettyPrint(w);
    }

    @Override
    public void accept(TypeCheckVisitor visitor) {
        i.accept(visitor);
        visitor.visit(this);
    }

    @Override
    public IRNode accept(IRTranslationVisitor visitor) {
        return visitor.visit(this);
    }
}
