package ast;

import edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;
import java_cup.runtime.ComplexSymbolFactory;

import java.util.List;

public class StmtDeclAssign extends Stmt {
    private List<TypeDecl> decls;
    private List<Expr> rhs;

    public StmtDeclAssign(List<TypeDecl> decls, List<Expr> rhs,
                          ComplexSymbolFactory.Location location) {
        super(location);
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
    public void accept(VisitorAST visitor) throws ASTException{
        for (Expr e : rhs) {
            e.accept(visitor);
        }
        visitor.visit(this);
    }
}
