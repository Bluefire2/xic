package ast;

public abstract class Expr implements Printable, ASTNode {
    ExprType e_type;//what kind of expression it is
    private TypeT typeCheckType;

    public ExprType getE_type() {
        return e_type;
    }

    public TypeT getTypeCheckType() {
        return typeCheckType;
    }

    public void setTypeCheckType(TypeT typeCheckType) {
        this.typeCheckType = typeCheckType;
    }
}