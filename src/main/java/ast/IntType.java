package ast;

import edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;

public class IntType extends Type {
    public IntType(){
    }

    public void prettyPrint(CodeWriterSExpPrinter w) {
        w.printAtom("int");
    }

    public boolean sameType(MetaType t) {
        return t instanceof IntType || t instanceof UnitType;
    }

    public boolean subtypeOf(MetaType t) {
        return t instanceof IntType || t instanceof UnitType;
    }
}