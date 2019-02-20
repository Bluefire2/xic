package ast;

import edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;

public class UnderscoreAssignable extends Assignable {

    public UnderscoreAssignable(int left, int right) {
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
