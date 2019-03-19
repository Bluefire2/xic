package ast;

import edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;

public class TypeTUnit extends TypeT {

    public void prettyPrint(CodeWriterSExpPrinter w) {
        w.printAtom("_");
    }

    @Override
    public String toString() {
        return "unit";
    }

    @Override
    public boolean subtypeOf(TypeT t) {
        return t instanceof TypeTUnit;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof TypeTUnit;
    }
}
