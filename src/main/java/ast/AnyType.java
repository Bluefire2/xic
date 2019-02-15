package ast;

import edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;

public class AnyType extends Type{
    AnyType() {
        this.t_type = TypeType.AnyType;
    }

    public void prettyPrint(CodeWriterSExpPrinter w) { }
}
