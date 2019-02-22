package ast;

import lexer.XiToken;

public interface ASTNode {
    void accept(VisitorAST visitor) throws ASTException;
    XiToken getToken();
}
