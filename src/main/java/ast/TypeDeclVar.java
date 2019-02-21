package ast;

import edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;
import polyglot.util.Pair;

import java.util.ArrayList;
import java.util.List;

public class TypeDeclVar extends TypeDecl {
    private Pair<String, TypeTTau> pair;

    public TypeDeclVar(Pair<String, TypeTTau> pair) {
        this.pair = pair;
    }

    @Override
    public void prettyPrint(CodeWriterSExpPrinter w) {
        w.startList();
        w.printAtom(pair.part1());
        pair.part2().prettyPrint(w);
        w.endList();
    }

    @Override
    public TypeT typeOf() {
        return pair.part2();
    }

    @Override
    public List<String> varsOf() {
        List<String> vars = new ArrayList<>(1);
        vars.add(pair.part1());
        return vars;
    }
}
