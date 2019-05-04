package kc875.ast;

import edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;
import edu.cornell.cs.cs4120.xic.ir.IRNode;
import java_cup.runtime.ComplexSymbolFactory;
import kc875.ast.visit.IRTranslationVisitor;
import kc875.ast.visit.TypeCheckVisitor;
import kc875.symboltable.TypeSymTable;
import polyglot.util.Pair;

import java.util.ArrayList;
import java.util.List;

public class FileProgram extends FileSource {
    private List<UseInterface> imports;
    private List<StmtDecl> globalDecls;
    private List<StmtDeclAssign> globalDefns;
    private List<ClassDefn> classDefns;
    private List<FuncDefn> funcDefns;
    private List<Pair<String, TypeSymTable>> signatures;

    public FileProgram(List<UseInterface> imports,
                       List<StmtDecl> globalDecls,
                       List<StmtDeclAssign> globalDefns,
                       List<ClassDefn> classDefns,
                       List<FuncDefn> funcDefns,
                       ComplexSymbolFactory.Location location) {
        super(location);
        this.imports = new ArrayList<>(imports);
        this.globalDecls = globalDecls;
        this.globalDefns = globalDefns;
        this.classDefns = classDefns;
        this.funcDefns = new ArrayList<>(funcDefns);
        this.signatures = new ArrayList<>();
        globalDecls.forEach((d) -> signatures.add(d.getSignature()));
        globalDefns.forEach((d) -> signatures.add(d.getSignature()));
        classDefns.forEach((d) -> signatures.add(d.getSignature()));
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
    public void accept(TypeCheckVisitor visitor) {
        visitor.visit(this);
        for (FuncDefn  d : funcDefns) {
            d.accept(visitor);
        }
    }

    @Override
    public IRNode accept(IRTranslationVisitor visitor) {
        return visitor.visit(this);
    }

    public List<Pair<String, TypeSymTable>> getSignatures() {
        return signatures;
    }
}
