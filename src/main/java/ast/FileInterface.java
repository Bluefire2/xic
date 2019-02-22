package ast;

import edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;
import java_cup.runtime.Symbol;
import symboltable.TypeSymTable;

import java.util.ArrayList;
import java.util.List;
import polyglot.util.Pair;

public class FileInterface extends FileSource {
    private List<FuncDecl> funcDecls;
    private List<Pair<String, TypeSymTable>> signatures;

    public FileInterface(List<FuncDecl> decls, Symbol token) {
        super(token);
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
    public void accept(VisitorAST visitor) throws ASTException {
        visitor.visit(this);
    }
}
