package ast;

import edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;

import java.util.List;

public class StmtDecl extends Stmt {
    private List<TypeDeclVar> decls;

    public StmtDecl(List<TypeDeclVar> decls, int left, int right) {
        super(left, right);
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
    public void accept(VisitorAST visitor)throws SemanticErrorException {
        visitor.visit(this);
    }
}