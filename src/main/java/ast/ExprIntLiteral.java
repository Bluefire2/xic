package ast;

import edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;
import java_cup.runtime.ComplexSymbolFactory;
import org.apache.commons.lang3.StringEscapeUtils;

public class ExprIntLiteral extends Expr {
    private Long value;
    private Character raw;
    public boolean isChar;

    public ExprIntLiteral(Long val, ComplexSymbolFactory.Location location) {
        super(location);
        this.value = val;
        this.e_type = ExprType.IntLiteralExpr;
        this.isChar = false;
    }

    public ExprIntLiteral(Character val,
                          ComplexSymbolFactory.Location location) {
        super(location);
        this.value = (long) Character.getNumericValue(val);
        this.e_type = ExprType.IntLiteralExpr;
        this.isChar = true;
        this.raw = val;
    }

    public Long getValue() {
        return value;
    }

    public void prettyPrint(CodeWriterSExpPrinter w) {
        if (this.isChar) {
            w.printAtom("\'"+ StringEscapeUtils.escapeJava(raw.toString())+"\'");
        } else {
            w.printAtom(value.toString());
        }
    }

    @Override
    public void accept(VisitorAST visitor) throws ASTException {
        visitor.visit(this);
    }
}
