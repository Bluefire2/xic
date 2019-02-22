package ast;

import java_cup.runtime.Symbol;
import lexer.XiToken;

public abstract class Assignable implements Printable, ASTNode {

    private XiToken token;// Lexed token

    public Assignable(Symbol s) {
        token = (XiToken) s;
    }

    @Override
    public XiToken getToken() {
        return token;
    }
}
