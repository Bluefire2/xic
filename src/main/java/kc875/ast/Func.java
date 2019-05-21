package kc875.ast;

import edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;
import java_cup.runtime.ComplexSymbolFactory;
import kc875.symboltable.TypeSymTable;
import polyglot.util.Pair;

import java.util.List;

public abstract class Func extends ASTNode implements Printable, TopLevelDecl {
    String name;
    List<Pair<String, TypeTTau>> params;
    TypeT output;
    Pair<String, TypeSymTable> signature;

    public Func(String name, List<Pair<String, TypeTTau>> params,
                TypeT output, ComplexSymbolFactory.Location location) {
        super(location);
        this.name = name;
        this.params = params;
        this.output = output;
    }

    public Func(String name, List<Pair<String, TypeTTau>> params,
                ComplexSymbolFactory.Location location) {
        this(name, params, new TypeTUnit(), location);
    }

    public String getName() {
        return name;
    }

    public List<Pair<String, TypeTTau>> getParams() {
        return params;
    }

    public TypeT getOutput() {
        return output;
    }

    boolean isProcedure() {
        return output instanceof TypeTUnit;
    }

    void printPair(Pair<String, TypeTTau> p, CodeWriterSExpPrinter w) {
        w.startList();
        w.printAtom(p.part1());
        p.part2().prettyPrint(w);
        w.endList();
    }

    public Pair<String, TypeSymTable> getSignature() {
        return signature;
    }

    public void setSignature(Pair<String, TypeSymTable> signature) {
        this.signature = signature;
    }
}
