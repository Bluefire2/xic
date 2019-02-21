package ast;

import edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;
import symboltable.TypeSymTable;

import java.util.ArrayList;
import java.util.List;
import polyglot.util.Pair;
import symboltable.TypeSymTableFunc;

public class InterfaceFile extends SourceFile {
    private List<FuncDecl> funcDecls;
    private List<Pair<String, TypeSymTable>> signatures;

    public InterfaceFile(List<FuncDecl> decls, int left, int right) {
        super(left, right);
        this.funcDecls = decls;
        this.signatures = new ArrayList<>();
        decls.forEach((d) -> signatures.add(d.getSignature()));
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

    @Override
    public void accept(VisitorAST visitor) throws SemanticErrorException {
        visitor.visit(this);
    }
}
