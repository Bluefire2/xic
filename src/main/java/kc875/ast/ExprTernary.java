package kc875.ast;

import edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;
import edu.cornell.cs.cs4120.xic.ir.IRNode;
import java_cup.runtime.ComplexSymbolFactory;
import kc875.ast.visit.IRTranslationVisitor;
import kc875.ast.visit.TypeCheckVisitor;

public class ExprTernary extends Expr {
    Expr e1;
    Expr e2;
    Expr e3;

    public ExprTernary(Expr e1, Expr e2, Expr e3, ComplexSymbolFactory.Location location) {
        super(location);
        this.e1 = e1;
        this.e2 = e2;
        this.e3 = e3;
    }

    @Override
    public void accept(TypeCheckVisitor visitor) {
        e1.accept(visitor);
        e2.accept(visitor);
        e3.accept(visitor);
        visitor.visit(this);
    }

    @Override
    public IRNode accept(IRTranslationVisitor visitor) {
        return visitor.visit(this);
    }

    @Override
    public void prettyPrint(CodeWriterSExpPrinter w) {
        w.startList();
        e1.prettyPrint(w);
        w.printAtom("?");
        e2.prettyPrint(w);
        w.printAtom(":");
        e3.prettyPrint(w);
        w.endList();
    }

    public Expr getCond() {
        return e1;
    }

    public Expr getTrueCase() {
        return e2;
    }

    public Expr getFalseCase() {
        return e3;
    }
}
