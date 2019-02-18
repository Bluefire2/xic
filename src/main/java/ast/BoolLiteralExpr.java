package ast;

import edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;

public class BoolLiteralExpr extends Expr {
    private Boolean value;

    public BoolLiteralExpr(Boolean val) {
        this.value = val;
        this.e_type = ExprType.BoolLiteralExpr;
    }

    public Boolean getValue() {
        return value;
    }

    public void prettyPrint(CodeWriterSExpPrinter w) {
        w.printAtom(value.toString());
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
