package kc875.ast;

import edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;
import edu.cornell.cs.cs4120.xic.ir.IRNode;
import java_cup.runtime.ComplexSymbolFactory;
import kc875.ast.visit.IRTranslationVisitor;
import kc875.ast.visit.TypeCheckVisitor;

//procedure methods
public class StmtMethodCall extends Stmt {
    private Expr obj;
    private ExprFunctionCall call;

    public StmtMethodCall(Expr obj, ExprFunctionCall call, ComplexSymbolFactory.Location location) {
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
        obj.accept(visitor);
        call.accept(visitor);
        visitor.visit(this);
    }

    @Override
    public IRNode accept(IRTranslationVisitor visitor) {
        return visitor.visit(this);
    }

    @Override
    public void prettyPrint(CodeWriterSExpPrinter w) {
        ExprBinop b = new ExprBinop(Binop.DOT, obj, call, this.getLocation());
        b.prettyPrint(w);
    }
}
