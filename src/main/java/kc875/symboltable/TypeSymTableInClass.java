package kc875.symboltable;

import kc875.ast.TypeTTauClass;

public class TypeSymTableInClass extends TypeSymTable {
    private TypeTTauClass typeTTauClass;

    public TypeSymTableInClass(TypeTTauClass t) {
        this.typeTTauClass = t;
    }

    public TypeTTauClass getTypeTTauClass() {
        return typeTTauClass;
    }
}
