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
    private List<StmtDecl> globalVars;
    private List<ClassDefn> classDefns;
    private List<FuncDefn> funcDefns;
    private List<Pair<String, TypeSymTable>> signatures;
    private List<Pair<String, ClassDecl>> classSignatures;

    public FileProgram(List<UseInterface> imports,
                       List<StmtDecl> globalVars,
                       List<ClassDefn> classDefns,
                       List<FuncDefn> funcDefns,
                       ComplexSymbolFactory.Location location) {
        super(location);
        this.imports = new ArrayList<>(imports);
        this.globalVars = globalVars;
        this.classDefns = classDefns;
        this.funcDefns = new ArrayList<>(funcDefns);
        this.signatures = new ArrayList<>();
        this.classSignatures = new ArrayList<>();

        funcDefns.forEach(d -> signatures.add(d.getSignature()));

        //TODO currently uses placeholder function names
        classDefns.forEach(d -> classSignatures.add(
                new Pair<>(d.getName(), d.toDecl())));

        //global vars are private to each module
//        globalDecls.forEach((d) -> {
//            Pair<String, TypeTTau> declType = d.getDecl().getPair();
//            signatures.add(new Pair<>(declType.part1(),
//                    new TypeSymTableVar(declType.part2())));
//        });
//
//        globalDefns.forEach((d) -> {
//            List<TypeDeclVar> typeDecls = d.getDecls();
//            for (TypeDeclVar t: typeDecls) {
//                Pair<String, TypeTTau> declType = t.getPair();
//                        signatures.add(new Pair<>(declType.part1(),
//                                new TypeSymTableVar(declType.part2())));
//            }
//        });


    }

    public FileProgram(List<UseInterface> imports,
                       List<TopLevelDecl> decls,
                       ComplexSymbolFactory.Location location) {
        super(location);
        this.imports = new ArrayList<>(imports);

        List<ClassDefn> classes = new ArrayList<>();
        List<FuncDefn> funcDefns = new ArrayList<>();
        List<StmtDecl> globalVars = new ArrayList<>();
        for (TopLevelDecl d : decls) {
            if (d instanceof FuncDefn) {
                funcDefns.add((FuncDefn) d);
            } else if (d instanceof ClassDefn) {
                classes.add((ClassDefn) d);
            } else if (d instanceof StmtDecl) {
                globalVars.add((StmtDecl) d);
            }
        }

        this.globalVars = globalVars;
        this.classDefns = classes;
        this.funcDefns = new ArrayList<>(funcDefns);
        this.signatures = new ArrayList<>();
        this.classSignatures = new ArrayList<>();

        funcDefns.forEach(d -> signatures.add(d.getSignature()));
        //TODO currently uses placeholder function names
        classDefns.forEach(d -> classSignatures.add(
                new Pair<>(d.getName(), d.toDecl())));
    }

    public List<UseInterface> getImports() {
        return imports;
    }

    public List<FuncDefn> getFuncDefns() {
        return funcDefns;
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
        globalVars.forEach((i) -> i.prettyPrint(w));
        w.endList();
        w.startUnifiedList();
        classDefns.forEach((i) -> i.prettyPrint(w));
        w.endList();
        w.startUnifiedList();
        funcDefns.forEach((d) -> d.prettyPrint(w));
        w.endList();
        w.endList();
    }

    @Override
    public void accept(TypeCheckVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public IRNode accept(IRTranslationVisitor visitor) {
        return visitor.visit(this);
    }

    public List<Pair<String, TypeSymTable>> getSignatures() {
        return signatures;
    }

    public List<Pair<String, ClassDecl>> getClassSignatures() {
        return classSignatures;
    }

    public List<StmtDecl> getGlobalVars() {
        return globalVars;
    }

    public List<ClassDefn> getClassDefns() {
        return classDefns;
    }
}
