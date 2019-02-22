package ast;

import edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;
import polyglot.util.Pair;
import symboltable.TypeSymTable;
import symboltable.TypeSymTableFunc;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

//funcDefns are for program files
public class FuncDefn implements Printable, ASTNode {
    private String name;
    private List<Pair<String, TypeTTau>> params;
    private Stmt body;
    private TypeT output;

    private int left;
    private int right;
    private Pair<String, TypeSymTable> signature;

    public FuncDefn(String name, List<Pair<String, TypeTTau>> params,
                    TypeT output, Stmt body, int left, int right) {
        this.name = name;
        this.params = params;
        this.output = output;
        this.body = body;
        this.left = left;
        this.right = right;

        List<TypeTTau> param_types = new ArrayList<>();
        params.forEach((p) -> param_types.add(p.part2()));

        TypeSymTable sig;
        if (param_types.size() == 0){
            sig = new TypeSymTableFunc(new TypeTUnit(), output);
        } else if (param_types.size() == 1) {
            sig = new TypeSymTableFunc(param_types.get(0), output);
        } else {
            sig = new TypeSymTableFunc(new TypeTList(param_types), output);
        }
        this.signature = new Pair<>(name, sig);
    }

    public FuncDefn(String name, List<Pair<String, TypeTTau>> params, Stmt body,
                                            int left, int right) {
        this(name, params, new TypeTUnit(), body, left, right);
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
    public void accept(VisitorAST visitor) throws ASTException {
        //cannot visit body here because of scoping
        visitor.visit(this);
    }

    @Override
    public int getLeft() {
        return left;
    }

    @Override
    public int getRight() {
        return right;
    }

    public Pair<String, TypeSymTable> getSignature() {
        return signature;
    }

    public void setSignature(Pair<String, TypeSymTable> signature) {
        this.signature = signature;
    }
}
