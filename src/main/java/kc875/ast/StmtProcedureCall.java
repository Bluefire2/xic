package kc875.ast;

import edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;
import edu.cornell.cs.cs4120.xic.ir.IRNode;
import java_cup.runtime.ComplexSymbolFactory;
import kc875.ast.visit.IRTranslationVisitor;
import kc875.ast.visit.TypeCheckVisitor;
import kc875.symboltable.TypeSymTableFunc;

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
    public void accept(TypeCheckVisitor visitor) {
        for (Expr e : args) {
            e.accept(visitor);
        }
        visitor.visit(this);
    }

    @Override
    public IRNode accept(IRTranslationVisitor visitor) {
        return visitor.visit(this);
    }

    public TypeSymTableFunc getSignature() {
        return signature;
    }

    public void setSignature(TypeSymTableFunc signature) {
        this.signature = signature;
    }
}
