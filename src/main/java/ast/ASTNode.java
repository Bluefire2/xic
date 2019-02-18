package ast;

public interface ASTNode {
    void accept(ASTVisitor visitor);
}
