package ast;

import edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;
import symboltable.TypeSymTable;

import java.util.ArrayList;
import java.util.List;
import polyglot.util.Pair;

public class InterfaceFile extends SourceFile {
    private List<FuncDecl> funcDecls;
    private List<Pair<String, TypeSymTable>> signatures;

    public InterfaceFile(List<FuncDecl> funcDecls, int left, int right) {
        super(left, right);
        this.funcDecls = new ArrayList<>(funcDecls);
    }

    public List<FuncDecl> getFuncDecls() {
        return funcDecls;
    }

    public void addFuncDecl(FuncDecl funcDecl) {
        funcDecls.add(funcDecl);
    }

    public boolean isInterface() {
        return true;
    }

    public void prettyPrint(CodeWriterSExpPrinter w) {
        w.startUnifiedList();
        w.startUnifiedList();
        funcDecls.forEach((i) -> i.prettyPrint(w));
        w.endList();
        w.endList();
    }

    public List<Pair<String, TypeSymTable>> getSignatures() {
        return signatures;
    }

    public void setSignatures(List<Pair<String, TypeSymTable>> signatures) {
        this.signatures = signatures;
    }

    @Override
    public void accept(VisitorAST visitor) throws SemanticErrorException {
        funcDecls.forEach((d) ->d.accept(visitor));
        visitor.visit(this);
    }
}
