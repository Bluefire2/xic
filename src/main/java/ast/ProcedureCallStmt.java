package ast;

import edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;

import java.util.List;

public class ProcedureCallStmt extends Stmt {
    private String name;
    private List<Expr> args;

    public ProcedureCallStmt(String name, List<Expr> args) {
        this.name = name;
        this.args = args;
        this.s_type = StmtType.ProcedureCallStmt;
    }

    public String getName() {
        return name;
    }

    public List<Expr> getArgs() {
        return args;
    }

    public void prettyPrint(CodeWriterSExpPrinter w) {
        w.startList();
        w.printAtom(name);
        args.forEach((a) -> a.prettyPrint(w));
        w.endList();
    }

    @Override
    public void accept(TypeCheckVisitor visitor) {
        args.forEach((e) -> e.accept(visitor));
        visitor.visit(this);
    }
}
