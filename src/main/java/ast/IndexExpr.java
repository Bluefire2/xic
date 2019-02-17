package ast;

import edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;

public class IndexExpr extends Expr {
    private Expr list;
    private Expr index;

    public IndexExpr(Expr list, Expr index) {
        this.list = list;
        this.index = index;
        this.e_type = ExprType.IndexExpr;
    }

    public Expr getList() {
        return list;
    }

    public Expr getIndex() {
        return index;
    }

    public void prettyPrint(CodeWriterSExpPrinter w) {
        w.startList();
        w.printAtom("[]");
        list.prettyPrint(w);
        index.prettyPrint(w);
        w.endList();
    }

    @Override
    public void accept(TypeCheckVisitor visitor) {
        list.accept(visitor);
        index.accept(visitor);
        visitor.visit(this);
    }
}
