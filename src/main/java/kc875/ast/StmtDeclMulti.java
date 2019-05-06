package kc875.ast;

import edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;
import edu.cornell.cs.cs4120.xic.ir.IRNode;
import java_cup.runtime.ComplexSymbolFactory;
import kc875.ast.visit.IRTranslationVisitor;
import kc875.ast.visit.TypeCheckVisitor;
import java.util.List;

//should be typechecked as multiple stmtdecls
public class StmtDeclMulti extends Stmt implements DeclOrDefn {
    private List<String> vars;
    private TypeTTau type;

    public StmtDeclMulti(List<String> vars, TypeTTau type,
                    ComplexSymbolFactory.Location location) {
        super(location);
        this.vars = vars;
        this.type = type;
        this.s_type = StmtType.DeclStmt;
    }

    public StmtDeclMulti(String var, TypeTTau type,
                         ComplexSymbolFactory.Location location) {
        super(location);
        this.vars = vars;
        this.type = type;
        this.s_type = StmtType.DeclStmt;
    }

    public void prettyPrint(CodeWriterSExpPrinter w) {
        w.startList();
        w.startUnifiedList();
        for (String name : vars) {
            w.printAtom(name);
        }
        w.endList();
        w.printAtom(type.toString());
        w.endList();
    }

    @Override
    public void accept(TypeCheckVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public IRNode accept(IRTranslationVisitor visitor) {
        return visitor.visit(this);
    }

    public List<String> getVars() {
        return vars;
    }

    public TypeTTau getType() {
        return type;
    }
}
