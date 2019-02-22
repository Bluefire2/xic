package ast;

import edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;

public class StmtWhile extends Stmt {
    private Expr guard;
    private Stmt doStmt;

    public StmtWhile(Expr guard, Stmt doStmt, int left, int right) {
        super(left, right);
        this.guard = guard;
        this.doStmt = doStmt;
        this.s_type = StmtType.WhileStmt;
    }

    public Expr getGuard() {
        return guard;
    }

    public Stmt getDoStmt() {
        return doStmt;
    }

    public void prettyPrint(CodeWriterSExpPrinter w) {
        w.startUnifiedList();
        w.printAtom("while");
        guard.prettyPrint(w);
        doStmt.prettyPrint(w);
        w.endList();
    }

    @Override
    public void accept(VisitorAST visitor) throws SemanticErrorException {
        guard.accept(visitor);
        doStmt.accept(visitor);
        visitor.visit(this);
    }
}