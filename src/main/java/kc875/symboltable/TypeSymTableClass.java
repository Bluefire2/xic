package kc875.symboltable;

import kc875.ast.*;

import java.util.Map;
import java.util.stream.Collectors;

public class TypeSymTableClass extends TypeSymTable {
    private Map<String, TypeSymTableVar> fields;
    private Map<String, TypeSymTableFunc> methods;

    private Map<String, TypeSymTableVar> fieldsOf(ClassDecl decl) {
        return decl.getFields().stream()
                .collect(Collectors.toMap(
                        StmtDecl::getName,
                        stmt -> new TypeSymTableVar((TypeTTau) stmt.getDecl().typeOf())
                ));
    }

    private Map<String, TypeSymTableVar> fieldsOf(ClassDefn defn) {
        // TODO
        return null;
    }

    private Map<String, TypeSymTableFunc> methodsOf(ClassDecl decl) {
        return decl.getMethods().stream()
                .collect(Collectors.toMap(
                        FuncDecl::getName,
                        funcDecl -> (TypeSymTableFunc) funcDecl.getSignature().part2()
                ));
    }

    private Map<String, TypeSymTableFunc> methodsOf(ClassDefn defn) {
        return defn.getMethods().stream()
                .collect(Collectors.toMap(
                        FuncDefn::getName,
                        funcDecl -> (TypeSymTableFunc) funcDecl.getSignature().part2()
                ));
    }

    public TypeSymTableClass(ClassDecl decl) {
        this.fields = fieldsOf(decl);
        this.methods = methodsOf(decl);
    }

    public TypeSymTableClass(ClassDefn defn) {
        this.fields = fieldsOf(defn);
        this.methods = methodsOf(defn);
    }

    public TypeSymTableClass(Map<String, TypeSymTableVar> fields,
                             Map<String, TypeSymTableFunc> methods) {
        this.fields = fields;
        this.methods = methods;
    }

    public Map<String, TypeSymTableVar> getFields() {
        return fields;
    }

    public Map<String, TypeSymTableFunc> getMethods() {
        return methods;
    }

    public boolean hasField(String name) {
        return fields.containsKey(name);
    }

    public boolean hasMethod(String name) {
        return methods.containsKey(name);
    }
}
