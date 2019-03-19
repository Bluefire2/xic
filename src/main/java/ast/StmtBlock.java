package ast;

import edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;
import edu.cornell.cs.cs4120.xic.ir.IRNode;
import java_cup.runtime.ComplexSymbolFactory;

import java.util.ArrayList;
import java.util.List;

public class StmtBlock extends Stmt {
    private List<Stmt> statements;

    public StmtBlock(List<Stmt> statements,
                     ComplexSymbolFactory.Location location) {
        super(location);
        this.statements = statements;
        this.s_type = StmtType.BlockStmt;
    }

    public StmtBlock(ComplexSymbolFactory.Location location) {
        this(new ArrayList<>(), location);
    }

    public List<Stmt> getStatments() {
        return statements;
    }

    public boolean isEmpty() {
        return statements.size() == 0;
    }

    public Stmt getLastStatement() {
        return statements.get(statements.size() - 1);
    }

    public void addStatement(Stmt s){
        statements.add(s);
    }

    public void prettyPrint(CodeWriterSExpPrinter w) {
        w.startUnifiedList();
        statements.forEach((s) -> s.prettyPrint(w));
        w.endList();

    }

    @Override
    public void accept(VisitorTypeCheck visitor) {
        visitor.visit(this);
    }

    @Override
    public IRNode accept(VisitorTranslation visitor) {
        return visitor.visit(this);
    }
}
