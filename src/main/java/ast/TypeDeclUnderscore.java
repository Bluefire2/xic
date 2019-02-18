package ast;

import edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;
import polyglot.util.Pair;

import java.util.ArrayList;
import java.util.List;

public class TypeDeclUnderscore extends TypeDecl {

    @Override
    public void prettyPrint(CodeWriterSExpPrinter w) {
        w.printAtom("_");
    }

    @Override
    TypeT typeOf(TypeDecl t) {
        return new TypeTUnit();
    }

    @Override
    List<String> varsOf(TypeDecl t) {
        return new ArrayList<>(0);
    }
}
