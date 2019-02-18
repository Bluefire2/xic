package ast;

public abstract class Expr implements Printable, ASTNode {
    ExprType e_type;//what kind of expression it is
    // TODO: do we need this; the Visitor is supposed to take care of ast
    //  node types
    TypeT typeCheckType;

    public ExprType getE_type() {
        return e_type;
    }

    public TypeT getTypeCheckType() {
        return typeCheckType;
    }

    public void setTypeCheckType(TypeT typecheckType) {
        this.typeCheckType = typecheckType;
    }
}