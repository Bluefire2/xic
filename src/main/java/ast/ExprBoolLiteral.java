package ast;

import ast.visit.IRTranslationVisitor;
import ast.visit.TypeCheckVisitor;
import edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;
import edu.cornell.cs.cs4120.xic.ir.IRNode;
import java_cup.runtime.ComplexSymbolFactory;

public class ExprBoolLiteral extends Expr {
    private Boolean value;

    public ExprBoolLiteral(Boolean val,
                           ComplexSymbolFactory.Location location) {
        super(location);
        this.value = val;
        this.e_type = ExprType.BoolLiteralExpr;
    }

    public Boolean getValue() {
        return value;
    }

    public void prettyPrint(CodeWriterSExpPrinter w) {
        w.printAtom(value.toString());
    }

    @Override
    public void accept(TypeCheckVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public IRNode accept(IRTranslationVisitor visitor) {
        return visitor.visit(this);
    }
}
