package ast;

import ast.visit.IRTranslationVisitor;
import ast.visit.TypeCheckVisitor;
import edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;
import edu.cornell.cs.cs4120.xic.ir.IRNode;
import java_cup.runtime.ComplexSymbolFactory;

public class StmtWhile extends Stmt {
    private Expr guard;
    private Stmt doStmt;

    public StmtWhile(Expr guard, Stmt doStmt,
                     ComplexSymbolFactory.Location location) {
        super(location);
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
    public void accept(TypeCheckVisitor visitor) {
        guard.accept(visitor);
        visitor.visit(this);
    }

    @Override
    public IRNode accept(IRTranslationVisitor visitor) {
        return visitor.visit(this);
    }
}
