package ast;

import edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;
import polyglot.util.Pair;

import java.util.ArrayList;
import java.util.List;

//funcDecls are for interfaces
public class FuncDecl implements Printable {
    private String name;
    private List<Pair<String, Type>> params;
    private List<Type> output;

    public FuncDecl(String name, List<Pair<String, Type>> params, List<Type> output) {
        this.name = name;
        this.params = params;
        this.output = output;
    }

    public FuncDecl(String name, List<Pair<String, Type>> params) {
        this.name = name;
        this.params = params;
        this.output = new ArrayList<Type>();
    }

    public String getName() {
        return name;
    }

    public List<Pair<String, Type>> getParams() {
        return params;
    }

    public List<Type> getOutput() {
        return output;
    }

    public boolean isProcedure() {
        return output.size()==0;
    }

    public void printPair(Pair<String, Type> p, CodeWriterSExpPrinter w){
        if (p.part2().getT_type() != TypeType.AnyType){
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
        w.endList();
    }
}
