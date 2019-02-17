package ast;

import edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;
import symboltable.CtxType;

import java.util.ArrayList;
import java.util.List;
import polyglot.util.Pair;

public class InterfaceFile extends SourceFile {
    private List<FuncDecl> funcDecls;
    private List<Pair<String,CtxType>> signatures;

    public InterfaceFile(List<FuncDecl> funcDecls) {
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

    public List<Pair<String, CtxType>> getSignatures() {
        return signatures;
    }

    public void setSignatures(List<Pair<String, CtxType>> signatures) {
        this.signatures = signatures;
    }

    @Override
    public void accept(TypeCheckVisitor visitor) {
        funcDecls.forEach((d) ->d.accept(visitor));
        visitor.visit(this);
    }
}
