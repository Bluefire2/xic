package ast;

import edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;

public class UnopExpr extends Expr {
    private Unop op;
    private Expr expr;

    UnopExpr(Unop op, Expr expr) {
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
}
