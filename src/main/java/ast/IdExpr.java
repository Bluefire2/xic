package ast;

import edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;

public class IdExpr extends Expr {
    private String name;

    public IdExpr(String name, int left, int right) {
        super(left, right);
        this.name = name;
        this.e_type = ExprType.IdExpr;
    }

    public String getName() {
        return name;
    }

    public void prettyPrint(CodeWriterSExpPrinter w) {
        w.printAtom(name);
    }

    @Override
    public void accept(VisitorAST visitor) throws SemanticErrorException {
        visitor.visit(this);
    }
}
