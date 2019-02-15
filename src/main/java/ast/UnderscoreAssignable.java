package ast;

import edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;

public class UnderscoreAssignable extends Assignable {
    @Override
    public void prettyPrint(CodeWriterSExpPrinter w) {
        w.printAtom("_");
    }
}
