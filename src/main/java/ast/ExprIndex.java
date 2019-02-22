package ast;

import edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;
import java_cup.runtime.Symbol;

public class ExprIndex extends Expr {
    private Expr array;
    private Expr index;

    public ExprIndex(Expr array, Expr index, Symbol token) {
        super(token);
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
    public void accept(VisitorAST visitor) throws ASTException{
        array.accept(visitor);
        index.accept(visitor);
        visitor.visit(this);
    }
}
