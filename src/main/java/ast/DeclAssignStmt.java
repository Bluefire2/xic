package ast;

import edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;

import java.util.List;

public class DeclAssignStmt extends Stmt {
    private List<TypeDecl> decls;
    private List<Expr> right;

    public DeclAssignStmt(List<TypeDecl> decls, List<Expr> right) {
        this.decls = decls;
        this.right = right;
        this.s_type = StmtType.DeclAssignStmt;
    }

    public List<TypeDecl> getDecls() {
        return decls;
    }

    public List<Expr> getRight() {
        return right;
    }

    public void prettyPrint(CodeWriterSExpPrinter w) {
        w.startList();
        w.printAtom("=");
        prettyPrintList(decls, w);
        prettyPrintList(right, w);
        w.endList();
    }

    @Override
    public void accept(VisitorAST visitor) {
        right.forEach((e) -> e.accept(visitor));
        visitor.visit(this);
    }
}
