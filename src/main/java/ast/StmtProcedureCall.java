package ast;

import edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;
import java_cup.runtime.ComplexSymbolFactory;
import symboltable.TypeSymTableFunc;

import java.util.List;

public class StmtProcedureCall extends Stmt {
    private String name;
    private List<Expr> args;
    private TypeSymTableFunc signature;


    public StmtProcedureCall(String name, List<Expr> args,
                             ComplexSymbolFactory.Location location) {
        super(location);
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
    public void accept(VisitorAST visitor) {
        for (Expr e : args) {
            e.accept(visitor);
        }
        visitor.visit(this);
    }

    public TypeSymTableFunc getSignature() {
        return signature;
    }

    public void setSignature(TypeSymTableFunc signature) {
        this.signature = signature;
    }
}
