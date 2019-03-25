package ast;

import ast.visit.IRTranslationVisitor;
import ast.visit.TypeCheckVisitor;
import edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;
import edu.cornell.cs.cs4120.xic.ir.IRNode;
import java_cup.runtime.ComplexSymbolFactory;

import java.util.ArrayList;
import java.util.List;

public class StmtReturn extends Stmt {
    private List<Expr> returnVals;

    public StmtReturn(List<Expr> returnVals,
                      ComplexSymbolFactory.Location location) {
        super(location);
        this.returnVals = returnVals;
        this.s_type = StmtType.FunctionReturnStmt;
    }

    public StmtReturn(ComplexSymbolFactory.Location location) {
        super(location);
        this.returnVals = new ArrayList<>();
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
    public void accept(TypeCheckVisitor visitor) {
        for (Expr e : returnVals) {
            e.accept(visitor);
        }
        visitor.visit(this);
    }

    @Override
    public IRNode accept(IRTranslationVisitor visitor) {
        return visitor.visit(this);
    }
}
