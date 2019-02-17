package ast;

import edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;
import polyglot.util.Pair;

import java.util.ArrayList;
import java.util.List;

//funcDefns are for program files
public class FuncDefn implements Printable, TypeCheckable {
    private String name;
    private List<Pair<String, Type>> params;
    private Stmt body;
    private List<Type> output;

    public FuncDefn(String name, List<Pair<String, Type>> params, List<Type> output, Stmt body) {
        this.name = name;
        this.params = params;
        this.output = output;
        this.body = body;
    }

    public FuncDefn(String name, List<Pair<String, Type>> params, Stmt body) {
        this.name = name;
        this.params = params;
        this.body = body;
        this.output = new ArrayList<Type>();
    }

    public String getName() {
        return name;
    }

    public List<Pair<String, Type>> getParams() {
        return params;
    }

    public Stmt getBody() {
        return body;
    }

    public List<Type> getOutput() {
        return output;
    }

    public boolean isProcedure() {
        return output.size()==0;
    }

    public void printPair(Pair<String, Type> p, CodeWriterSExpPrinter w){
        if (p.part2() instanceof UnitType){
            w.startList();
            w.printAtom(p.part1());
            p.part2().prettyPrint(w);
            w.endList();
        } else {
            w.printAtom("_");
        }
    }

    public void prettyPrint(CodeWriterSExpPrinter w){
        w.startList();
        w.printAtom(name);
        w.startList();
        params.forEach((p) -> this.printPair(p, w));
        w.endList();
        w.startList();
        output.forEach((t) -> t.prettyPrint(w));
        w.endList();
        body.prettyPrint(w);
        w.endList();
    }

    @Override
    public void accept(TypeCheckVisitor visitor) {
        //TODO
    }
}
