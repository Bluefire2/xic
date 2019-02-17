package ast;

import edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;
import polyglot.util.Pair;

import java.util.List;

public class DeclStmt extends Stmt {
    private List<Pair<String, Type>> decls;

    public DeclStmt(List<Pair<String, Type>> decls) {
        this.decls = decls;
        this.s_type = StmtType.DeclStmt;
    }

    public List<Pair<String, Type>> getDecls() {
        return decls;
    }

    public void prettyPrint(CodeWriterSExpPrinter w) {
        if (decls.size() == 1){
            this.printPair(decls.get(0),w);
        } else {
            w.startList();
            decls.forEach((d) -> this.printPair(d, w));
            w.endList();
        }
    }

    @Override
    public void accept(TypeCheckVisitor visitor) {
        visitor.visit(this);
    }
}
