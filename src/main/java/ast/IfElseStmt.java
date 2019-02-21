package ast;

import edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;

public class IfElseStmt extends Stmt {
    private Expr guard;
    private Stmt thenStmt;
    private Stmt elseStmt;

    public IfElseStmt(Expr guard, Stmt thenStmt, Stmt elseStmt, int left, int right) {
        super(left, right);
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
    public void accept(VisitorAST visitor) throws SemanticErrorException {
        guard.accept(visitor);
        thenStmt.accept(visitor);
        elseStmt.accept(visitor);
        visitor.visit(this);
    }

    public Expr getGuard() {
        return guard;
    }

    public Stmt getThenStmt() {
        return thenStmt;
    }

    public Stmt getElseStmt() {
        return thenStmt;
    }
}
