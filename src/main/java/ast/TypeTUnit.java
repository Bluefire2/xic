package ast;

import edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;

public class TypeTUnit extends TypeT {

    public void prettyPrint(CodeWriterSExpPrinter w) {
        w.printAtom("_");
    }

    public boolean subtypeOf(TypeT t) {
        throw new Error("not implemented: subtyping tau");
    }
}
