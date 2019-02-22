package ast;

import edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;

import java.util.ArrayList;
import java.util.List;

public class StmtReturn extends Stmt {
    private List<Expr> returnVals;

    public StmtReturn(List<Expr> returnVals, int left, int right) {
        super(left, right);
        this.returnVals = returnVals;
        this.s_type = StmtType.FunctionReturnStmt;
    }

    public StmtReturn(int left, int right) {
        super(left, right);
        this.returnVals = new ArrayList<Expr>();
        this.s_type = StmtType.ProcedureReturnStmt;
    }

    public List<Expr> getReturnVals() {
        return returnVals;
    }

    public void prettyPrint(CodeWriterSExpPrinter w) {
        w.startList();
        w.printAtom("return");
        returnVals.forEach((v) -> v.prettyPrint(w));
        w.endList();
    }

    @Override
    public void accept(VisitorAST visitor) throws SemanticErrorException {
        for (Expr e : returnVals) {
            e.accept(visitor);
        }
        visitor.visit(this);
    }
}
