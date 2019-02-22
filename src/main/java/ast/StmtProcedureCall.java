package ast;

import edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;

import java.util.List;

public class StmtProcedureCall extends Stmt {
    private String name;
    private List<Expr> args;

    public StmtProcedureCall(String name, List<Expr> args, int left, int right) {
        super(left, right);
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
    public void accept(VisitorAST visitor) throws ASTException {
        for (Expr e : args) {
            e.accept(visitor);
        }
        visitor.visit(this);
    }
}
