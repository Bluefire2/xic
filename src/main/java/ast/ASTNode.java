package ast;

public interface ASTNode {
    void accept(VisitorAST visitor);
}