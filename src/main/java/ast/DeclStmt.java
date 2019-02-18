package ast;

import edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;

import java.util.List;

public class DeclStmt extends Stmt {
    private List<TypeDeclVar> decls;

    public DeclStmt(List<TypeDeclVar> decls) {
        this.decls = decls;
        this.s_type = StmtType.DeclStmt;
    }

    public List<TypeDeclVar> getDecls() {
        return decls;
    }

    public void prettyPrint(CodeWriterSExpPrinter w) {
        prettyPrintList(decls, w);
    }

    @Override
    public void accept(VisitorAST visitor) {
        visitor.visit(this);
    }
}
