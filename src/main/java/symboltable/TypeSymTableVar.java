package symboltable;

import ast.TypeTTau;

public class TypeSymTableVar extends TypeSymTable {
    private TypeTTau typeTTau;

    public TypeSymTableVar(TypeTTau t) {
        this.typeTTau = t;
    }

    public TypeTTau getTypeTTau() {
        return typeTTau;
    }
}
