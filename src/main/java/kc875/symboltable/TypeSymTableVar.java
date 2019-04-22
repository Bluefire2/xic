package kc875.symboltable;

import kc875.ast.TypeTTau;

public class TypeSymTableVar extends TypeSymTable {
    private TypeTTau typeTTau;

    public TypeSymTableVar(TypeTTau t) {
        this.typeTTau = t;
    }

    public TypeTTau getTypeTTau() {
        return typeTTau;
    }
}
