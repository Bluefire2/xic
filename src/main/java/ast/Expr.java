package ast;

public abstract class Expr implements Printable, ASTNode {
    ExprType e_type;//what kind of expression it is
    private TypeT typeCheckType;

    int left;
    int right;

    public Expr(int left, int right) {
        this.left = left;
        this.right = right;
    }

    public ExprType getE_type() {
        return e_type;
    }

    public TypeT getTypeCheckType() {
        return typeCheckType;
    }

    public void setTypeCheckType(TypeT typeCheckType) {
        this.typeCheckType = typeCheckType;
    }

    @Override
    public int getLeft() {
        return left;
    }

    @Override
    public int getRight() {
        return right;
    }
}