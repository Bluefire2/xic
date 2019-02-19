package ast;

//top level "nodes"
public abstract class SourceFile implements Printable, ASTNode {
    abstract boolean isInterface();

    int left;
    int right;

    public SourceFile(int left, int right) {
        this.left = left;
        this.right = right;
    }

    @Override
    public int getLeft() {
        return left;
    }

    @Override
    public int getRight() {
        return right;
    }
}
