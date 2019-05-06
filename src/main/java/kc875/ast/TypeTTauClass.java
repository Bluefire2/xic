package kc875.ast;

import edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;
import kc875.utils.Maybe;

public class TypeTTauClass extends TypeTTau {
    private String name;
    // superClass is unknown if this class is not derived from another class
    private Maybe<String> superClass;

    public TypeTTauClass(String name, Maybe<String> superClass) {
        this.name = name;
        this.superClass = superClass;
    }

    public TypeTTauClass(String name) {
        this.name = name;
        this.superClass = Maybe.unknown();
    }

    public TypeTTauClass(String name, String superClass) {
        this.name = name;
        this.superClass = Maybe.definitely(superClass);
    }

    public String getName() {
        return name;
    }

    public Maybe<String> getSuperClass() {
        return superClass;
    }

    @Override
    public void prettyPrint(CodeWriterSExpPrinter w) {
        // TODO: do we need this?
//        w.printAtom(name + superClass.to(sc -> " extends " + sc.name).otherwise(""));
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
        // TODO: do we need this?
        // name extends superClass or just name
//        return name + superClass.to(sc -> " extends " + sc.name).otherwise("");
        return name;
    }
}
