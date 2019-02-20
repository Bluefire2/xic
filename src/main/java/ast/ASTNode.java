package ast;

public interface ASTNode {
    void accept(VisitorAST visitor) throws SemanticErrorException;
    int getLeft();
    int getRight();
}
