package ast;

import edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;
import org.apache.commons.lang3.StringEscapeUtils;

public class IntLiteralExpr extends Expr {
    private Long value;
    private Character raw;
    public boolean isChar;

    public IntLiteralExpr(Long val) {
        this.value = val;
        this.e_type = ExprType.IntLiteralExpr;
        this.isChar = false;
    }

    public IntLiteralExpr(Character val) {
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
}
