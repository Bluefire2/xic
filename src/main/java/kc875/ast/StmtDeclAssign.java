package kc875.ast;

import edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;
import edu.cornell.cs.cs4120.xic.ir.IRNode;
import java_cup.runtime.ComplexSymbolFactory;
import kc875.ast.visit.IRTranslationVisitor;
import kc875.ast.visit.TypeCheckVisitor;

import java.util.List;

public class StmtDeclAssign extends Stmt {
    private List<TypeDecl> decls;
    private Expr rhs;

    public StmtDeclAssign(List<TypeDecl> decls, Expr rhs,
                          ComplexSymbolFactory.Location location) {
        super(location);
        this.decls = decls;
        this.rhs = rhs;
        this.s_type = StmtType.DeclAssignStmt;
    }

    public List<TypeDecl> getDecls() {
        return decls;
    }

    public Expr getRhs() {
        return rhs;
    }

    public void prettyPrint(CodeWriterSExpPrinter w) {
        w.startList();
        w.printAtom("=");
        prettyPrintList(decls, w);
        rhs.prettyPrint(w);
        w.endList();
    }

    @Override
    public void accept(TypeCheckVisitor visitor) {
        rhs.accept(visitor);
        visitor.visit(this);
    }

    @Override
    public IRNode accept(IRTranslationVisitor visitor) {
        return visitor.visit(this);
    }
}
