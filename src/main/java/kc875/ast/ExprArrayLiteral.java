package kc875.ast;

import edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;
import edu.cornell.cs.cs4120.xic.ir.IRNode;
import java_cup.runtime.ComplexSymbolFactory;
import kc875.ast.visit.IRTranslationVisitor;
import kc875.ast.visit.TypeCheckVisitor;
import org.apache.commons.lang3.StringEscapeUtils;

import java.util.ArrayList;
import java.util.List;

public class ExprArrayLiteral extends Expr {
    private List<Expr> contents;
    private boolean isString;
    private String raw;

    public ExprArrayLiteral(List<Expr> contents,
                            ComplexSymbolFactory.Location location) {
        super(location);
        this.contents = contents;
        this.e_type = ExprType.ListLiteralExpr;
        this.isString = false;
    }

    public ExprArrayLiteral(String value,
                            ComplexSymbolFactory.Location location) {
        super(location);
        char[] chars = value.toCharArray();
        this.contents = new ArrayList<>();
        for (char c : chars)
            contents.add(new ExprIntLiteral(c, location));
        this.isString = true;
        this.raw = value;
    }

    public List<Expr> getContents() {
        return contents;
    }

    public int getLength() {
        return contents.size();
    }

    public void prettyPrint(CodeWriterSExpPrinter w) {
        if (this.isString) {
            w.printAtom("\""+ StringEscapeUtils.escapeJava(raw) +"\"");
        } else {
            w.startList();
            contents.forEach((e) -> e.prettyPrint(w));
            w.endList();
        }
    }

    @Override
    public void accept(TypeCheckVisitor visitor) {
        for (Expr e : contents) {
            e.accept(visitor);
        }
        visitor.visit(this);
    }

    @Override
    public IRNode accept(IRTranslationVisitor visitor) {
        return visitor.visit(this);
    }
}
