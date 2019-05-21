package kc875.ast;

import edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;

public class TypeTTauClass extends TypeTTau {
    private String name;

    public TypeTTauClass(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public void prettyPrint(CodeWriterSExpPrinter w) {
        w.printAtom(name);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TypeTTauClass that = (TypeTTauClass) o;
        return name.equals(that.name);
    }

    @Override
    public String toString() {
        return name;
    }
}
