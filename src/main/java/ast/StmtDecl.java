package ast;

import edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;
import java_cup.runtime.ComplexSymbolFactory;

import java.util.List;

public class StmtDecl extends Stmt {
    private TypeDeclVar decl;

    public StmtDecl(List<TypeDeclVar> decls,
                    ComplexSymbolFactory.Location location) {
        super(location);
        this.decl = decl;
        this.s_type = StmtType.DeclStmt;
    }

    public TypeDeclVar getDecls() {
        return decl;
    }

    public void prettyPrint(CodeWriterSExpPrinter w) {
        decl.prettyPrint(w);
    }

    @Override
    public void accept(VisitorAST visitor) {
        visitor.visit(this);
    }
}
