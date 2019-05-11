package kc875.ast;

import edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;
import edu.cornell.cs.cs4120.xic.ir.IRNode;
import java_cup.runtime.ComplexSymbolFactory;
import kc875.ast.visit.IRTranslationVisitor;
import kc875.ast.visit.TypeCheckVisitor;

public class AssignableFieldAccess extends Assignable {
    private ExprFieldAccess access;

    public AssignableFieldAccess(ExprFieldAccess access,
                                 ComplexSymbolFactory.Location location) {
        super(location);
        this.access = access;
    }

    public ExprFieldAccess getAccess() {
        return access;
    }

    @Override
    public void prettyPrint(CodeWriterSExpPrinter w) {
        access.prettyPrint(w);
    }

    @Override
    public void accept(TypeCheckVisitor visitor) {
        access.accept(visitor);
        visitor.visit(this);
    }

    @Override
    public IRNode accept(IRTranslationVisitor visitor) {
        return visitor.visit(this);
    }
}
