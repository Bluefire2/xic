package ast;

import edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;

public class TypeTUnit extends TypeT {

    public void prettyPrint(CodeWriterSExpPrinter w) {
        w.printAtom("_");
    }

    @Override
    public boolean subtypeOf(TypeT t) {
        return t instanceof TypeTUnit;
    }
}
