package ast;

import edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;

public class AssignableUnderscore extends Assignable {

    public AssignableUnderscore(int left, int right) {
        super(left, right);
    }

    @Override
    public void prettyPrint(CodeWriterSExpPrinter w) {
        w.printAtom("_");
    }

    @Override
    public void accept(VisitorAST visitor) throws SemanticErrorException {
        // TODO
    }
}
