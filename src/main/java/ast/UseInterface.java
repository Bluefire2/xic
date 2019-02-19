package ast;

import edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;

public class UseInterface implements ASTNode {
    private String name;

    private int left;
    private int right;

    public UseInterface(String name, int left, int right) {
        this.left = left;
        this.right = right;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void prettyPrint(CodeWriterSExpPrinter w) {
        w.startList();
        w.printAtom("use");
        w.printAtom(name);
        w.endList();
    }

    @Override
    public void accept(VisitorAST visitor) {
        visitor.visit(this);
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
