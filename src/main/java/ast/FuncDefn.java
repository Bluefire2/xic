package ast;

import edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;
import polyglot.util.Pair;

import java.util.List;
import java.util.stream.Collectors;

//funcDefns are for program files
public class FuncDefn implements Printable, ASTNode {
    private String name;
    private List<Pair<String, TypeTTau>> params;
    private Stmt body;
    private TypeT output;

    public FuncDefn(String name, List<Pair<String, TypeTTau>> params,
                    TypeT output, Stmt body) {
        this.name = name;
        this.params = params;
        this.output = output;
        this.body = body;
    }

    public FuncDefn(String name, List<Pair<String, TypeTTau>> params, Stmt body) {
        this(name, params, new TypeTUnit(), body);
    }

    public String getName() {
        return name;
    }

    public List<Pair<String, TypeTTau>> getParams() {
        return params;
    }

    public TypeTList getParamTypes() {
        return new TypeTList(
                params.stream().map(Pair::part2).collect(Collectors.toList())
        );
    }

    public Stmt getBody() {
        return body;
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
            // output type not unit
            output.prettyPrint(w);
        w.endList();
        body.prettyPrint(w);
        w.endList();
    }

    @Override
    public void accept(VisitorAST visitor) {
        //TODO
    }
}
