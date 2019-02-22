package ast;

import java_cup.runtime.Symbol;
import lexer.XiToken;

public abstract class Expr implements Printable, ASTNode {
    ExprType e_type;//what kind of expression it is
    private TypeT typeCheckType;

    private XiToken token;// Lexed token

    public Expr(Symbol s) {
        token = (XiToken) s;
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

    @Override
    public XiToken getToken() {
        return token;
    }
}