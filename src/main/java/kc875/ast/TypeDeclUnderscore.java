package kc875.ast;

import edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;

import java.util.ArrayList;
import java.util.List;

public class TypeDeclUnderscore extends TypeDecl {

    @Override
    public void prettyPrint(CodeWriterSExpPrinter w) {
        w.printAtom("_");
    }

    @Override
    public TypeT typeOf() {
        return new TypeTUnit();
    }

    @Override
    public List<String> varsOf() {
        return new ArrayList<>(0);
    }
}
