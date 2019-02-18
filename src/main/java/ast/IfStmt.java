package ast;

import edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;

public class IfStmt extends Stmt {
    private Expr guard;
    private Stmt thenStmt;

    public IfStmt(Expr guard, Stmt thenStmt) {
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
    public void accept(ASTVisitor visitor) {
        guard.accept(visitor);
        thenStmt.accept(visitor);
        visitor.visit(this);
    }
}
