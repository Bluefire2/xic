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

//funcDefns are for program files
public class FuncDefn extends Func {
    private Stmt body;

    public FuncDefn(String name, List<Pair<String, TypeTTau>> params,
                    TypeT output, Stmt body,
                    ComplexSymbolFactory.Location location) {
        super(name, params, output, location);
        this.body = body;

        List<TypeTTau> param_types = new ArrayList<>();
        params.forEach((p) -> param_types.add(p.part2()));

        TypeSymTable sig;
        switch (param_types.size()) {
            case 0:
                sig = new TypeSymTableFunc(new TypeTUnit(), output, false);
                break;
            case 1:
                sig = new TypeSymTableFunc(param_types.get(0), output, false);
                break;
            default:
                sig = new TypeSymTableFunc(new TypeTList(param_types), output, false);
        }
        this.signature = new Pair<>(name, sig);
    }

    public FuncDefn(String name, List<Pair<String, TypeTTau>> params, Stmt body,
                    ComplexSymbolFactory.Location location) {
        this(name, params, new TypeTUnit(), body, location);
    }

    public Stmt getBody() {
        return body;
    }

    public void prettyPrint(CodeWriterSExpPrinter w) {
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
    public void accept(TypeCheckVisitor visitor) {
        //cannot visit body here because of scoping
        visitor.visit(this);
    }

    @Override
    public IRNode accept(IRTranslationVisitor visitor) {
        return visitor.visit(this);
    }

    /**
     * Create a corresponding function declaration for this definition.
     *
     * @return The declaration.
     */
    public FuncDecl toDecl() {
        return new FuncDecl(name, params, output, getLocation());
    }
}
