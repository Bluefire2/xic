package kc875.ast;

import edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;
import edu.cornell.cs.cs4120.xic.ir.IRNode;
import java_cup.runtime.ComplexSymbolFactory;
import kc875.ast.visit.IRTranslationVisitor;
import kc875.ast.visit.TypeCheckVisitor;

public class StmtIfElse extends Stmt {
    private Expr guard;
    private Stmt thenStmt;
    private Stmt elseStmt;

    public StmtIfElse(Expr guard, Stmt thenStmt, Stmt elseStmt,
                      ComplexSymbolFactory.Location location) {
        super(location);
        this.guard = guard;
        this.thenStmt = thenStmt;
        this.elseStmt = elseStmt;
        this.s_type = StmtType.IfElseStmt;
    }

    public void prettyPrint(CodeWriterSExpPrinter w) {
        w.startUnifiedList();
        w.printAtom("if");
        guard.prettyPrint(w);
        thenStmt.prettyPrint(w);
        elseStmt.prettyPrint(w);
        w.endList();
    }

    @Override
    public void accept(TypeCheckVisitor visitor) {
        guard.accept(visitor);
        visitor.visit(this);
    }

    @Override
    public IRNode accept(IRTranslationVisitor visitor) {
        return visitor.visit(this);
    }

    public Expr getGuard() {
        return guard;
    }

    public Stmt getThenStmt() {
        return thenStmt;
    }

    public Stmt getElseStmt() {
        return elseStmt;
    }
}
