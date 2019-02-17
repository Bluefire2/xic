package ast;

import edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;
import polyglot.util.Pair;

import java.util.List;

public class DeclAssignStmt extends Stmt {
    private List<Pair<String, Type>> decls;
    private List<Expr> right;

    public DeclAssignStmt(List<Pair<String, Type>> decls, List<Expr> right) {
        this.decls = decls;
        this.right = right;
        this.s_type = StmtType.DeclAssignStmt;
    }

    public List<Pair<String, Type>> getDecls() {
        return decls;
    }

    public List<Expr> getRight() {
        return right;
    }

    public void prettyPrint(CodeWriterSExpPrinter w) {
        w.startList();
        w.printAtom("=");
        if (decls.size() == 1){
            this.printPair(decls.get(0),w);
        } else {
            w.startList();
            decls.forEach((d) -> this.printPair(d,w));
            w.endList();
        }
        if (right.size() == 1){
            right.get(0).prettyPrint(w);
        } else {
            w.startList();
            right.forEach((e) -> e.prettyPrint(w));
            w.endList();
        }
        w.endList();
    }

    @Override
    public void accept(TypeCheckVisitor visitor) {
        right.forEach((e) -> e.accept(visitor));
        visitor.visit(this);
    }
}
