package ast;

import ast.visit.IRTranslationVisitor;
import ast.visit.TypeCheckVisitor;
import edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;
import edu.cornell.cs.cs4120.xic.ir.IRNode;
import java_cup.runtime.ComplexSymbolFactory;
import org.apache.commons.lang3.StringEscapeUtils;

public class ExprIntLiteral extends Expr {
    private Long value;
    private Character raw;
    private boolean isChar;

    public ExprIntLiteral(Long val, ComplexSymbolFactory.Location location) {
        super(location);
        this.value = val;
        this.e_type = ExprType.IntLiteralExpr;
        this.isChar = false;
    }

    public ExprIntLiteral(Character val,
                          ComplexSymbolFactory.Location location) {
        super(location);
        this.value = (long) val;
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
        } else if (value == Long.MIN_VALUE) {
            w.startList();
            w.printAtom("-");
            w.printAtom(value.toString().substring(1));
            w.endList();
        } else {
            w.printAtom(value.toString());
        }
    }

    @Override
    public void accept(TypeCheckVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public IRNode accept(IRTranslationVisitor visitor) {
        return visitor.visit(this);
    }
}
