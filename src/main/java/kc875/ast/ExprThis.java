package kc875.ast;

import edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;
import edu.cornell.cs.cs4120.xic.ir.IRNode;
import java_cup.runtime.ComplexSymbolFactory;
import kc875.ast.visit.IRTranslationVisitor;
import kc875.ast.visit.TypeCheckVisitor;

public class ExprThis extends Expr {
    public ExprThis(ComplexSymbolFactory.Location location) {
        super(location);
    }
    public ExprThis(String cName, ComplexSymbolFactory.Location location) {
        super(location);
        this.setTypeCheckType(new TypeTTauClass(cName));
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
        w.printAtom("this");
    }
}
