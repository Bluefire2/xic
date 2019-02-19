package ast;

public abstract class Assignable implements Printable, ASTNode {
    int left;
    int right;

    public Assignable(int left, int right) {
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
