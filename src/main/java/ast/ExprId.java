package ast;

import edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;
import java_cup.runtime.Symbol;

public class ExprId extends Expr {
    private String name;

    public ExprId(String name, Symbol token) {
        super(token);
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
    public void accept(VisitorAST visitor) throws ASTException {
        visitor.visit(this);
    }
}
