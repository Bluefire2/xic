package ast;

import java_cup.runtime.Symbol;
import lexer.XiToken;

//top level "nodes"
public abstract class FileSource implements Printable, ASTNode {
    abstract boolean isInterface();

    private XiToken token;// Lexed token

    public FileSource(Symbol s) {
        token = (XiToken) s;
    }

    @Override
    public XiToken getToken() {
        return token;
    }

}
