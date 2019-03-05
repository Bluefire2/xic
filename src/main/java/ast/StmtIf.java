package ast;

import edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;
import edu.cornell.cs.cs4120.xic.ir.IRNode;
import java_cup.runtime.ComplexSymbolFactory;

public class StmtIf extends Stmt {
    private Expr guard;
    private Stmt thenStmt;

    public StmtIf(Expr guard, Stmt thenStmt,
                  ComplexSymbolFactory.Location location) {
        super(location);
        this.guard = guard;
        this.thenStmt = thenStmt;
        this.s_type = StmtType.IfStmt;
    }

    public void prettyPrint(CodeWriterSExpPrinter w) {
        w.startUnifiedList();
        w.printAtom("if");
        guard.prettyPrint(w);
        thenStmt.prettyPrint(w);
        w.endList();
    }

    @Override
    public void accept(VisitorTypeCheck visitor) {
        guard.accept(visitor);
        visitor.visit(this);
    }

    @Override
    public IRNode accept(VisitorTranslation visitor) {
        return visitor.visit(this);
    }

    public Expr getGuard() {
        return guard;
    }

    public Stmt getThenStmt() {
        return thenStmt;
    }
}
