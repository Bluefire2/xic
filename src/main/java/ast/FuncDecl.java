package ast;

import edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;
import polyglot.util.Pair;

import java.util.List;

//funcDecls are for interfaces
public class FuncDecl implements Printable, ASTNode {
    private String name;
    private List<Pair<String, TypeTTau>> params;
    private TypeT output;

    private int left;
    private int right;

    public FuncDecl(String name, List<Pair<String, TypeTTau>> params,
                    TypeT output, int left, int right) {
        this.name = name;
        this.params = params;
        this.output = output;
        this.left = left;
        this.right = right;
    }

    public FuncDecl(String name, List<Pair<String, TypeTTau>> params,
                                        int left, int right) {
        this(name, params, new TypeTUnit(), left, right);
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

    public boolean isProcedure() {
        return output instanceof TypeTUnit;
    }

    private void printPair(Pair<String, TypeTTau> p, CodeWriterSExpPrinter w){
        w.startList();
        w.printAtom(p.part1());
        p.part2().prettyPrint(w);
        w.endList();
    }

    public void prettyPrint(CodeWriterSExpPrinter w){
        w.startList();
        w.printAtom(name);
        w.startList();
        params.forEach(p -> printPair(p, w));
        w.endList();
        w.startList();
        if (!isProcedure())
            output.prettyPrint(w);
        w.endList();
        w.endList();
    }

    @Override
    public void accept(VisitorAST visitor) throws SemanticErrorException {
        //TODO;
    }

    @Override
    public int getLeft() {
        return left;
    }

    @Override
    public int getRight() {
        return right;
    }
}
