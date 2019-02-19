package ast;

import edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;

public class BinopExpr extends Expr {
    private Binop op;
    private Expr leftExpr;
    private Expr rightExpr;

    public BinopExpr(Binop op, Expr leftExpr, Expr rightExpr, int left, int right) {
        super(left, right);
        this.op = op;
        this.leftExpr = leftExpr;
        this.rightExpr = rightExpr;
        this.e_type = ExprType.BinopExpr;
    }

    public Binop getOp() {
        return op;
    }

    public Expr getLeftExpr() {
        return leftExpr;
    }

    public Expr getRightExpr() {
        return rightExpr;
    }

    public String opToString(){
        String opString = "";
        switch (op) {
            case PLUS: opString = "+"; break;
            case MINUS: opString = "-"; break;
            case MULT: opString = "*"; break;
            case HI_MULT: opString = "*>>"; break;
            case DIV: opString = "/"; break;
            case MOD: opString = "%"; break;
            case EQEQ: opString = "=="; break;
            case NEQ: opString = "!="; break;
            case GT: opString = ">"; break;
            case LT: opString = "<"; break;
            case GTEQ: opString = ">="; break;
            case LTEQ: opString = "<="; break;
            case AND: opString = "&"; break;
            case OR: opString = "|"; break;
        }
        return opString;
    }

    public void prettyPrint(CodeWriterSExpPrinter w) {
        w.startList();
        w.printAtom(this.opToString());
        leftExpr.prettyPrint(w);
        rightExpr.prettyPrint(w);
        w.endList();
    }

    @Override
    public void accept(VisitorAST visitor) {
        leftExpr.accept(visitor);
        rightExpr.accept(visitor);
        visitor.visit(this);
    }


}
