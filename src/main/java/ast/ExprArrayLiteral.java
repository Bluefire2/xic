package ast;

import edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;
import java_cup.runtime.ComplexSymbolFactory;
import java_cup.runtime.Symbol;
import org.apache.commons.lang3.StringEscapeUtils;

import java.util.ArrayList;
import java.util.List;

public class ExprArrayLiteral extends Expr {
    private List<Expr> contents;
    public boolean isString;
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
    public void accept(VisitorAST visitor) throws ASTException {
        for (Expr e : contents) {
            e.accept(visitor);
        }
        visitor.visit(this);
    }
}
