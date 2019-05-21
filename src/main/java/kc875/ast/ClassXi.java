package kc875.ast;

import java_cup.runtime.ComplexSymbolFactory;
import kc875.utils.Maybe;

import java.util.List;

public abstract class ClassXi extends ASTNode implements Printable, TopLevelDecl {
    String name;
    Maybe<String> superClass;
    List<StmtDecl> fields;
    // Methods without bodies, even if class implements some methods
    List<FuncDecl> methodDecls;

    ClassXi(String name,
            Maybe<String> superClass,
            List<StmtDecl> fields,
            List<FuncDecl> methodDecls,
            ComplexSymbolFactory.Location location) {
        super(location);
        this.name = name;
        this.superClass = superClass;
        this.fields = fields;
        this.methodDecls = methodDecls;
    }

    ClassXi(String name,
            List<StmtDecl> fields,
            List<FuncDecl> methodDecls,
            ComplexSymbolFactory.Location location) {
        this(name, Maybe.unknown(), fields, methodDecls, location);
    }

    ClassXi(String name,
            String superClass,
            List<StmtDecl> fields,
            List<FuncDecl> methodDecls,
            ComplexSymbolFactory.Location location) {
        this(name, Maybe.definitely(superClass), fields, methodDecls, location);
    }

    public String getName() {
        return name;
    }

    public List<FuncDecl> getMethodDecls() {
        return methodDecls;
    }

    public Maybe<String> getSuperClass() {
        return superClass;
    }

    public List<StmtDecl> getFields() {
        return fields;
    }
}
