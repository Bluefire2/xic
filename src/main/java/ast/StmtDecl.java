package ast;

import ast.visit.IRTranslationVisitor;
import ast.visit.TypeCheckVisitor;
import edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;
import edu.cornell.cs.cs4120.xic.ir.IRNode;
import java_cup.runtime.ComplexSymbolFactory;

public class StmtDecl extends Stmt {
    private TypeDeclVar decl;

    public StmtDecl(TypeDeclVar decl,
                    ComplexSymbolFactory.Location location) {
        super(location);
        this.decl = decl;
        this.s_type = StmtType.DeclStmt;
    }

    public TypeDeclVar getDecl() {
        return decl;
    }

    public void prettyPrint(CodeWriterSExpPrinter w) {
        decl.prettyPrint(w);
    }

    @Override
    public void accept(TypeCheckVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public IRNode accept(IRTranslationVisitor visitor) {
        return visitor.visit(this);
    }
}
