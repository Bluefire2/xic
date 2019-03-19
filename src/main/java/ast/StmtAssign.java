package ast;

import edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;
import edu.cornell.cs.cs4120.xic.ir.IRNode;
import java_cup.runtime.ComplexSymbolFactory;

public class StmtAssign extends Stmt {
    private Assignable lhs;
    private Expr rhs;

    public StmtAssign(Assignable lhs, Expr rhs,
                      ComplexSymbolFactory.Location location) {
        super(location);
        this.lhs = lhs;
        this.rhs = rhs;
        this.s_type = StmtType.AssignStmt;
    }

    public Assignable getLhs() {
        return lhs;
    }

    public Expr getRhs() {
        return rhs;
    }

    public void prettyPrint(CodeWriterSExpPrinter w) {
        w.startList();
        w.printAtom("=");
        lhs.prettyPrint(w);
        rhs.prettyPrint(w);
        w.endList();
    }

    @Override
    public void accept(VisitorTypeCheck visitor) {
        lhs.accept(visitor);
        rhs.accept(visitor);
        visitor.visit(this);
    }

    @Override
    public IRNode accept(VisitorTranslation visitor) {
        return visitor.visit(this);
    }

}
