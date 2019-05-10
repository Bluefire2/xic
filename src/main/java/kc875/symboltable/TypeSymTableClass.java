package kc875.symboltable;

import kc875.ast.*;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class TypeSymTableClass extends TypeSymTable {
    private TypeTTauClass type;
    private Map<String, TypeSymTableVar> fields;
    private Map<String, TypeSymTableFunc> methods;

    private static Map<String, TypeSymTableVar> fieldsOf(ClassDecl decl) {
        Map<String, TypeSymTableVar> fields = new HashMap<>();
        decl.getFields().forEach(f ->
                f.applyToAll((String name, TypeTTau type) ->
                        fields.put(name, new TypeSymTableVar(type))));
        return fields;
    }

    private static Map<String, TypeSymTableVar> fieldsOf(ClassDefn defn) {
        return fieldsOf(defn.toDecl());
    }

    private static Map<String, TypeSymTableFunc> methodsOf(ClassDecl decl) {
        return decl.getMethods().stream()
                .collect(Collectors.toMap(
                        FuncDecl::getName,
                        funcDecl -> (TypeSymTableFunc) funcDecl.getSignature().part2()
                ));
    }

    private static Map<String, TypeSymTableFunc> methodsOf(ClassDefn defn) {
        return defn.getMethods().stream()
                .collect(Collectors.toMap(
                        FuncDefn::getName,
                        funcDecl -> (TypeSymTableFunc) funcDecl.getSignature().part2()
                ));
    }

    public TypeSymTableClass(TypeTTauClass type, ClassDecl decl) {
        this.type = type;
        this.fields = fieldsOf(decl);
        this.methods = methodsOf(decl);
    }

    public TypeSymTableClass(TypeTTauClass type, ClassDefn defn) {
        this.type = type;
        this.fields = fieldsOf(defn);
        this.methods = methodsOf(defn);
    }

    public TypeSymTableClass(TypeTTauClass type,
                             Map<String, TypeSymTableVar> fields,
                             Map<String, TypeSymTableFunc> methods) {
        this.type = type;
        this.fields = fields;
        this.methods = methods;
    }

    public TypeTTauClass getType() {
        return type;
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
