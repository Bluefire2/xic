package ast;

import edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;
import java_cup.runtime.ComplexSymbolFactory;

public class ExprUnop extends Expr {
    private Unop op;
    private Expr expr;

    public ExprUnop(Unop op, Expr expr,
                    ComplexSymbolFactory.Location location) {
        super(location);
        this.op = op;
        this.expr = expr;
        this.e_type = ExprType.UnopExpr;
    }

    public Unop getOp() {
        return op;
    }

    public Expr getExpr() {
        return expr;
    }

    public String opToString(){
        String opString = "";
        switch (op) {
            case NOT: opString = "!"; break;
            case UMINUS: opString = "-"; break;
        }
        return opString;
    }

    public void prettyPrint(CodeWriterSExpPrinter w) {
        w.startList();
        w.printAtom(this.opToString());
        expr.prettyPrint(w);
        w.endList();
    }

    @Override
    public void accept(VisitorAST visitor) {
        expr.accept(visitor);
        visitor.visit(this);
    }
}
