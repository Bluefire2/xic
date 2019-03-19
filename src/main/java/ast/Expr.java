package ast;

import edu.cornell.cs.cs4120.xic.ir.IRNode;
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

    TypeT getTypeCheckType() {
        return typeCheckType;
    }

    void setTypeCheckType(TypeT typeCheckType) {
        this.typeCheckType = typeCheckType;
    }
}