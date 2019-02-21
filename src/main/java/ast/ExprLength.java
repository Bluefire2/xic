package ast;

import edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;

public class ExprLength extends Expr {
    private Expr list;

    public ExprLength(Expr list, int left, int right) {
        super(left, right);
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
    public void accept(VisitorAST visitor) throws SemanticErrorException {
        list.accept(visitor);
        visitor.visit(this);
    }
}
