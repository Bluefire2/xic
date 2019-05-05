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

public class FileInterface extends FileSource {
    private List<UseInterface> imports;
    private List<ClassDecl> classes;
    private List<FuncDecl> funcDecls;
    private List<Pair<String, TypeSymTable>> signatures;
    private List<Pair<String, ClassDecl>> class_signatures;

    public FileInterface(List<UseInterface> imports, List<ClassDecl> classes, List<FuncDecl> decls,
                         ComplexSymbolFactory.Location location) {
        super(location);
        this.imports = imports;
        this.classes = classes;
        this.funcDecls = decls;
        this.signatures = new ArrayList<>();
        this.class_signatures = new ArrayList<>();
        //TODO currently uses placeholders
        decls.forEach((d) -> signatures.add(d.getSignature()));
        classes.forEach((d) -> class_signatures.add(new Pair<>(d.getName(), d)));
    }

    public FileInterface(List<UseInterface> imports,
                         List<DeclOrDefn> decls,
                         ComplexSymbolFactory.Location location){
        super(location);
        List<ClassDecl> classes = new ArrayList<>();
        List<FuncDecl> funcDecls = new ArrayList<>();
        for (DeclOrDefn d : decls) {
            if (d instanceof FuncDecl) {
                funcDecls.add((FuncDecl) d);
            } else if (d instanceof ClassDecl) {
                classes.add((ClassDecl) d);
            }
        }
        this.imports = imports;
        this.classes = classes;
        this.funcDecls = funcDecls;
        this.signatures = new ArrayList<>();
        this.class_signatures = new ArrayList<>();
        //TODO currently uses placeholders
        funcDecls.forEach((d) -> signatures.add(d.getSignature()));
        classes.forEach((d) -> class_signatures.add(new Pair<>(d.getName(), d)));
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
    public void accept(TypeCheckVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public IRNode accept(IRTranslationVisitor visitor) {
        return visitor.visit(this);
    }

    public List<Pair<String, ClassDecl>> getClassSignatures() {
        return class_signatures;
    }
}
