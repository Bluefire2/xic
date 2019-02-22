package ast;

import edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;

public class StmtIf extends Stmt {
    private Expr guard;
    private Stmt thenStmt;

    public StmtIf(Expr guard, Stmt thenStmt, int left, int right) {
        super(left, right);
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
    public void accept(VisitorAST visitor) throws SemanticErrorException {
        guard.accept(visitor);
        thenStmt.accept(visitor);
        visitor.visit(this);
    }

    public Expr getGuard() {
        return guard;
    }

    public Stmt getThenStmt() {
        return thenStmt;
    }
}