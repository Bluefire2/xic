package ast;

import edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;

public class BoolLiteralExpr extends Expr {
    private Boolean value;

    public BoolLiteralExpr(Boolean val, int left, int right) {
        super(left, right);
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
    public void accept(VisitorAST visitor) throws SemanticErrorException{
        visitor.visit(this);
    }
}
