package kc875.ast;

import edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;
import edu.cornell.cs.cs4120.xic.ir.IRNode;
import java_cup.runtime.ComplexSymbolFactory;
import kc875.ast.visit.IRTranslationVisitor;
import kc875.ast.visit.TypeCheckVisitor;
import kc875.symboltable.TypeSymTable;
import kc875.symboltable.TypeSymTableFunc;
import polyglot.util.Pair;

import java.util.ArrayList;
import java.util.List;

//funcDecls are for interfaces
public class FuncDecl extends Func {
    public FuncDecl(String name, List<Pair<String, TypeTTau>> params,
                    TypeT output, ComplexSymbolFactory.Location location) {
        super(name, params, output, location);

        List<TypeTTau> param_types = new ArrayList<>();
        params.forEach((p) -> param_types.add(p.part2()));

        TypeSymTable sig;
        switch (param_types.size()) {
            case 0:
                sig = new TypeSymTableFunc(new TypeTUnit(), output);
                break;
            case 1:
                sig = new TypeSymTableFunc(param_types.get(0), output);
                break;
            default:
                sig = new TypeSymTableFunc(new TypeTList(param_types), output);
        }
        this.signature = new Pair<>(name, sig);
    }

    public FuncDecl(String name, List<Pair<String, TypeTTau>> params,
                    ComplexSymbolFactory.Location location) {
        this(name, params, new TypeTUnit(), location);
    }

    public void prettyPrint(CodeWriterSExpPrinter w) {
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
    public void accept(TypeCheckVisitor visitor) {
        //do nothing
    }

    @Override
    public IRNode accept(IRTranslationVisitor visitor) {
        return visitor.visit(this);
    }
}
