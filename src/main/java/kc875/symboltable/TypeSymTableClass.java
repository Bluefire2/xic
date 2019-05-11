package kc875.symboltable;

import kc875.ast.ClassXi;
import kc875.ast.FuncDecl;
import kc875.ast.TypeTTau;
import kc875.ast.TypeTTauClass;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class TypeSymTableClass extends TypeSymTable {
    private TypeTTauClass type;
    private Map<String, TypeSymTableVar> fields;
    private Map<String, TypeSymTableFunc> methods;

    private static Map<String, TypeSymTableVar> fieldsOf(ClassXi classXi) {
        Map<String, TypeSymTableVar> fields = new HashMap<>();
        classXi.getFields().forEach(f ->
                f.applyToAll((String name, TypeTTau type) ->
                        fields.put(name, new TypeSymTableVar(type))));
        return fields;
    }

    private static Map<String, TypeSymTableFunc> methodsOf(ClassXi classXi) {
        return classXi.getMethodDecls().stream()
                .collect(Collectors.toMap(
                        FuncDecl::getName,
                        funcDecl -> (TypeSymTableFunc) funcDecl.getSignature().part2()
                ));
    }

    public TypeSymTableClass(TypeTTauClass type,
                             Map<String, TypeSymTableVar> fields,
                             Map<String, TypeSymTableFunc> methods) {
        this.type = type;
        this.fields = fields;
        this.methods = methods;
    }

    public TypeSymTableClass(TypeTTauClass type, ClassXi classXi) {
        this(type, fieldsOf(classXi), methodsOf(classXi));
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
