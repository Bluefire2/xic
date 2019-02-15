package ast;

import edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;
import org.apache.commons.lang3.StringEscapeUtils;

import java.util.ArrayList;
import java.util.List;

public class ListLiteralExpr extends Expr {
    private List<Expr> contents;
    public boolean isString;
    private String raw;

    ListLiteralExpr(List<Expr> contents) {
        this.contents = contents;
        this.e_type = ExprType.ListLiteralExpr;
        this.isString = false;
    }

    ListLiteralExpr(String value) {
        char[] chars = value.toCharArray();
        this.contents = new ArrayList<>();
        for (int i = 0; i < chars.length; i++) {
            contents.add(new IntLiteralExpr(chars[i]));
        }
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
}
