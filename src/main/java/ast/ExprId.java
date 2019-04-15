package ast;

import ast.visit.IRTranslationVisitor;
import ast.visit.TypeCheckVisitor;
import edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;
import edu.cornell.cs.cs4120.xic.ir.IRNode;
import java_cup.runtime.ComplexSymbolFactory;

public class ExprId extends Expr {
    private String name;

    public ExprId(String name, ComplexSymbolFactory.Location location) {
        super(location);
        this.name = name;
        this.e_type = ExprType.IdExpr;
    }

    public String getName() {
        return name;
    }

    public void prettyPrint(CodeWriterSExpPrinter w) {
        w.printAtom(name);
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
