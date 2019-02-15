package ast;

import edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;

import java.util.ArrayList;
import java.util.List;

public class ReturnStmt extends Stmt {
    private List<Expr> returnVals;

    public ReturnStmt(List<Expr> returnVals) {
        this.returnVals = returnVals;
        this.s_type = StmtType.FunctionReturnStmt;
    }

    public ReturnStmt() {
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
}
