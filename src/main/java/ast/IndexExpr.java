package ast;

import edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;

public class IndexExpr extends Expr {
    private Expr array;
    private Expr index;

    public IndexExpr(Expr array, Expr index, int left, int right) {
        super(left, right);
        this.array = array;
        this.index = index;
        this.e_type = ExprType.IndexExpr;
    }

    public Expr getArray() {
        return array;
    }

    public Expr getIndex() {
        return index;
    }

    public void prettyPrint(CodeWriterSExpPrinter w) {
        w.startList();
        w.printAtom("[]");
        array.prettyPrint(w);
        index.prettyPrint(w);
        w.endList();
    }

    @Override
    public void accept(VisitorAST visitor) throws SemanticErrorException{
        array.accept(visitor);
        index.accept(visitor);
        visitor.visit(this);
    }
}
