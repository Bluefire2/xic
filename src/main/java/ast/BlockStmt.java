package ast;

import edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;

import java.util.ArrayList;
import java.util.List;

public class BlockStmt extends Stmt {
    private List<Stmt> statements;

    public BlockStmt(List<Stmt> statements) {
        this.statements = statements;
        this.s_type = StmtType.BlockStmt;
    }

    public BlockStmt() {
        this(new ArrayList<>());
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

    public void prettyPrint(CodeWriterSExpPrinter w) {
        w.startUnifiedList();
        statements.forEach((s) -> s.prettyPrint(w));
        w.endList();
    }
}