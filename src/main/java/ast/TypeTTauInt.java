package ast;

import edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;

public class TypeTTauInt extends TypeTTau {

    @Override
    public boolean equals(Object obj) {
        return obj instanceof TypeTTauInt;
    }

    @Override
    public String toString() {
        return "int";
    }

    public void prettyPrint(CodeWriterSExpPrinter w) {
        w.printAtom("int");
    }

    public boolean sameType(TypeT t) {
        return t instanceof TypeTTauInt || t instanceof TypeTUnit;
    }

    public boolean subtypeOf(TypeT t) {
        return t instanceof TypeTTauInt || t instanceof TypeTUnit;
    }
}