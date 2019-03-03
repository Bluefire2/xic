package ast;

import edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;
import java_cup.runtime.ComplexSymbolFactory;

public class AssignableUnderscore extends Assignable {

    public AssignableUnderscore(ComplexSymbolFactory.Location location) {
        super(location);
    }

    @Override
    public void prettyPrint(CodeWriterSExpPrinter w) {
        w.printAtom("_");
    }

    @Override
    public void accept(VisitorAST visitor) { }
}
