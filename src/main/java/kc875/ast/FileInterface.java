package kc875.ast;

import edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;
import edu.cornell.cs.cs4120.util.InternalCompilerError;
import edu.cornell.cs.cs4120.xic.ir.IRNode;
import java_cup.runtime.ComplexSymbolFactory;
import kc875.ast.visit.IRTranslationVisitor;
import kc875.ast.visit.TypeCheckVisitor;
import kc875.symboltable.TypeSymTable;
import polyglot.util.Pair;

import java.util.ArrayList;
import java.util.List;

public class FileInterface extends FileSource {
    private List<ClassDecl> classDecls;
    private List<FuncDecl> funcDecls;
    private List<Pair<String, TypeSymTable>> signatures;
    private List<Pair<String, ClassDecl>> class_signatures;

    public FileInterface(List<UseInterface> imports,
                         List<ClassDecl> classDecls,
                         List<FuncDecl> decls,
                         ComplexSymbolFactory.Location location) {
        super(imports, location);
        this.classDecls = classDecls;
        this.funcDecls = decls;
        this.signatures = new ArrayList<>();
        this.class_signatures = new ArrayList<>();
        decls.forEach((d) -> signatures.add(d.getSignature()));
        classDecls.forEach((d) -> class_signatures.add(new Pair<>(d.getName(), d)));
    }

    public FileInterface(List<UseInterface> imports,
                         List<TopLevelDecl> decls,
                         ComplexSymbolFactory.Location location) {
        super(imports, location);
        List<ClassDecl> classes = new ArrayList<>();
        List<FuncDecl> funcDecls = new ArrayList<>();
        for (TopLevelDecl d : decls) {
            if (d instanceof FuncDecl) {
                funcDecls.add((FuncDecl) d);
            } else if (d instanceof ClassDecl) {
                classes.add((ClassDecl) d);
            } else {
                throw new InternalCompilerError(
                        d.toString() + " not allowed as a top level " +
                                "declaration in a FileProgram"
                );
            }
        }

        this.classDecls = classes;
        this.funcDecls = funcDecls;
        this.signatures = new ArrayList<>();
        this.class_signatures = new ArrayList<>();
        funcDecls.forEach((d) -> signatures.add(d.getSignature()));
        classes.forEach((d) -> class_signatures.add(new Pair<>(d.getName(), d)));
    }

    public List<FuncDecl> getFuncDecls() {
        return funcDecls;
    }

    public void prettyPrint(CodeWriterSExpPrinter w) {
        w.startUnifiedList();
        if (!imports.isEmpty()) {
            w.startUnifiedList();
            imports.forEach((i) -> i.prettyPrint(w));
            w.endList();
        }
        if (!classDecls.isEmpty()) {
            w.startUnifiedList();
            classDecls.forEach((i) -> i.prettyPrint(w));
            w.endList();
        }
        w.startUnifiedList();
        funcDecls.forEach((i) -> i.prettyPrint(w));
        w.endList();
        w.endList();
    }

    public List<ClassDecl> getClassDecls() {
        return classDecls;
    }

    @Override
    public void accept(TypeCheckVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public IRNode accept(IRTranslationVisitor visitor) {
        return visitor.visit(this);
    }
}
