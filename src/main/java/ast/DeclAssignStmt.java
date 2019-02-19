package ast;

import edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;

import java.util.List;

public class DeclAssignStmt extends Stmt {
    private List<TypeDecl> decls;
    private List<Expr> rhs;

    public DeclAssignStmt(List<TypeDecl> decls, List<Expr> rhs, int left, int right) {
        super(left, right);
        this.decls = decls;
        this.rhs = rhs;
        this.s_type = StmtType.DeclAssignStmt;
    }

    public List<TypeDecl> getDecls() {
        return decls;
    }

    public List<Expr> getRhs() {
        return rhs;
    }

    public void prettyPrint(CodeWriterSExpPrinter w) {
        w.startList();
        w.printAtom("=");
        prettyPrintList(decls, w);
        prettyPrintList(rhs, w);
        w.endList();
    }

    @Override
    public void accept(VisitorAST visitor) {
        rhs.forEach((e) -> e.accept(visitor));
        visitor.visit(this);
    }
}
