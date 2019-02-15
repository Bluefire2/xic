package ast;

public abstract class Expr implements Printable {
    ExprType e_type;

    public ExprType getE_type() {
        return e_type;
    }
}
