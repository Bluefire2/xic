package ast;

import edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;

public class UnitType extends Type implements MetaType{
    public UnitType(){ }

    public void prettyPrint(CodeWriterSExpPrinter w) { }

    public boolean subtypeOf(Type t) {
        return false;
    }
}
