package kc875.ast;

import edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;

public interface Printable {
    void prettyPrint(CodeWriterSExpPrinter w);
}
