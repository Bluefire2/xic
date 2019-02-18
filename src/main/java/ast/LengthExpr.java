package ast;

import edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;

public class LengthExpr extends Expr {
    private Expr list;

    public LengthExpr(Expr list) {
        this.list = list;
        this.e_type = ExprType.LengthExpr;
    }

    public Expr getList() {
        return list;
    }

    public void prettyPrint(CodeWriterSExpPrinter w) {
        w.startList();
        w.printAtom("length");
        list.prettyPrint(w);
        w.endList();
    }

    @Override
    public void accept(ASTVisitor visitor) {
        list.accept(visitor);
        visitor.visit(this);
    }
}
