package kc875.ast;

import edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;
import edu.cornell.cs.cs4120.xic.ir.IRNode;
import java_cup.runtime.ComplexSymbolFactory;
import kc875.ast.visit.IRTranslationVisitor;
import kc875.ast.visit.TypeCheckVisitor;

public class ExprNew extends Expr {
    TypeTTauClass classType;
    ExprFunctionCall contructor;

    public ExprNew(TypeTTauClass classType, ExprFunctionCall contructor,
                   ComplexSymbolFactory.Location location) {
        super(location);
        this.classType = classType;
        this.contructor = contructor;
    }

    @Override
    public void accept(TypeCheckVisitor visitor) {
        visitor.visit(contructor);
        visitor.visit(this);
    }

    @Override
    public IRNode accept(IRTranslationVisitor visitor) {
       return visitor.visit(this);
    }

    @Override
    public void prettyPrint(CodeWriterSExpPrinter w) {
        w.startList();
        w.printAtom("new");
        classType.prettyPrint(w);
        w.endList();
    }

    public TypeTTauClass getClassType() {
        return classType;
    }

    public ExprFunctionCall getContructor() {
        return contructor;
    }
}
