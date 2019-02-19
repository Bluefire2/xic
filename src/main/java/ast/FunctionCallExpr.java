package ast;

import edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;

import java.util.List;

public class FunctionCallExpr extends Expr {
    private String name;
    private List<Expr> args;

    public FunctionCallExpr(String name, List<Expr> args, int left, int right) {
        super(left, right);
        this.name = name;
        this.args = args;
        this.e_type = ExprType.FunctionCallExpr;
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
    public void accept(VisitorAST visitor) {
        args.forEach((e) -> e.accept(visitor));
        visitor.visit(this);
    }
}
