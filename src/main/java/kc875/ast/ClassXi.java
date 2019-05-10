package kc875.ast;

import java_cup.runtime.ComplexSymbolFactory;
import kc875.utils.Maybe;

import java.util.List;

public abstract class ClassXi extends ASTNode implements Printable, TopLevelDecl {
    String name;
    Maybe<String> superClass;
    List<StmtDecl> fields;

    ClassXi(String name,
            Maybe<String> superClass,
            List<StmtDecl> fields,
            ComplexSymbolFactory.Location location) {
        super(location);
        this.name = name;
        this.superClass = superClass;
        this.fields = fields;
    }

    ClassXi(String name,
            List<StmtDecl> fields,
            ComplexSymbolFactory.Location location) {
        this(name, Maybe.unknown(), fields, location);
    }

    ClassXi(String name,
            String superClass,
            List<StmtDecl> fields,
            ComplexSymbolFactory.Location location) {
        this(name, Maybe.definitely(superClass), fields, location);
    }

    public String getName() {
        return name;
    }

    public Maybe<String> getSuperClass() {
        return superClass;
    }

    public List<StmtDecl> getFields() {
        return fields;
    }
}
