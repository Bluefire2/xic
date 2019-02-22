package ast;

import edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;
import java_cup.runtime.Symbol;

public class AssignableUnderscore extends Assignable {

    public AssignableUnderscore(Symbol token) {
        super(token);
    }

    @Override
    public void prettyPrint(CodeWriterSExpPrinter w) {
        w.printAtom("_");
    }

    @Override
    public void accept(VisitorAST visitor) throws ASTException {
        // TODO
    }
}
