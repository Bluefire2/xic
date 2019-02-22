package ast;

public interface ASTNode {
    void accept(VisitorAST visitor) throws ASTException;
    int getLeft();
    int getRight();
}
