package kc875.ast;

import edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;
import kc875.utils.Maybe;

public class TypeTTauClass extends TypeTTau {
    private String name;
    // superClass is unknown if this class is not derived from another class
    private Maybe<TypeTTauClass> superClass;

    public TypeTTauClass(String name) {
        this.name = name;
        this.superClass = Maybe.unknown();
    }

    public TypeTTauClass(String name, TypeTTauClass superClass) {
        this.name = name;
        this.superClass = Maybe.definitely(superClass);
    }

    @Override
    public boolean subtypeOf(TypeT t) {
        if (t instanceof TypeTUnit)
            // everything is a sub type of unit
            return true;

        if (!superClass.isKnown())
            // super class unknown ==> this class is a "non-derived" class
            // ==> cannot be a sub type of any class
            return false;

        if (!(t instanceof TypeTTauClass))
            // if t is not unit, it must be at least a class
            return false;
        TypeTTauClass c = (TypeTTauClass) t;

        // Either this is the same as c or
        // - if sc is known, sc is a subtype of c
        // - if sc is unknown, then this is not a subtype of c
        // (isKnown check at the end implements this)
        return name.equals(c.name)
                || superClass.to(sc -> sc.subtypeOf(c)).isKnown();
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
