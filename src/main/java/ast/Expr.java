package ast;

import java_cup.runtime.ComplexSymbolFactory;

public abstract class Expr extends ASTNode implements Printable {
    ExprType e_type;//what kind of expression it is
    private TypeT typeCheckType;

    public Expr(ComplexSymbolFactory.Location location) {
        super(location);
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
}