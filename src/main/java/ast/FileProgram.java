package ast;

import edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;
import polyglot.util.Pair;
import symboltable.TypeSymTable;
import symboltable.TypeSymTableFunc;

import java.util.ArrayList;
import java.util.List;

public class FileProgram extends SourceFile {
    private List<UseInterface> imports;
    private List<FuncDefn> funcDefns;
    private List<Pair<String, TypeSymTable>> signatures;

    public FileProgram(List<UseInterface> imports, List<FuncDefn> funcDefns, int left, int right) {
        super(left, right);
        this.imports = new ArrayList<>(imports);
        this.funcDefns = new ArrayList<>(funcDefns);
        this.signatures = new ArrayList<>();
        funcDefns.forEach((d) -> signatures.add(d.getSignature()));
    }

    public List<UseInterface> getImports() {
        return imports;
    }

    public List<FuncDefn> getFuncDefns() {
        return funcDefns;
    }

    public void addFuncDefn(FuncDefn funcDefn) {
        funcDefns.add(funcDefn);
    }

    public void addImport(UseInterface use) {
        imports.add(use);
    }

    public boolean isInterface() {
        return false;
    }

    public void prettyPrint(CodeWriterSExpPrinter w) {
        w.startUnifiedList();
        w.startUnifiedList();
        imports.forEach((i) -> i.prettyPrint(w));
        w.endList();
        w.startUnifiedList();
        funcDefns.forEach((d) -> d.prettyPrint(w));
        w.endList();
        w.endList();
    }

    @Override
    public void accept(VisitorAST visitor) throws SemanticErrorException {
        visitor.visit(this);
        for (FuncDefn  d : funcDefns) {
            d.accept(visitor);
        }
    }

    public List<Pair<String, TypeSymTable>> getSignatures() {
        return signatures;
    }
}