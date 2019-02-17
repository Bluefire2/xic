package ast;

import edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;

public class BoolType extends Type{
    public BoolType() {
    }

    public void prettyPrint(CodeWriterSExpPrinter w) {
        w.printAtom("bool");
    }

    public boolean sameType(MetaType t) {
        return t instanceof BoolType;
    }

    public boolean subtypeOf(MetaType t) {
        return t instanceof BoolType || t instanceof UnitType;
    }
}
