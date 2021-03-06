package kc875.ast;

import edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;
import edu.cornell.cs.cs4120.xic.ir.IRNode;
import java_cup.runtime.ComplexSymbolFactory;
import kc875.ast.visit.IRTranslationVisitor;
import kc875.ast.visit.TypeCheckVisitor;

//methods that return things
public class ExprMethodCall extends Expr {
    private Expr obj;
    private ExprFunctionCall call;

    public ExprMethodCall(Expr obj, ExprFunctionCall call,
                          ComplexSymbolFactory.Location location) {
        super(location);
        this.obj = obj;
        this.call = call;
    }

    public Expr getObj() {
        return obj;
    }

    public ExprFunctionCall getCall() {
        return call;
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
        call.prettyPrint(w);
        w.endList();
    }
}
