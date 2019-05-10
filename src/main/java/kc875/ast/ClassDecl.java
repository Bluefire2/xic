package kc875.ast;

import edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;
import edu.cornell.cs.cs4120.xic.ir.IRNode;
import java_cup.runtime.ComplexSymbolFactory;
import kc875.ast.visit.IRTranslationVisitor;
import kc875.ast.visit.TypeCheckVisitor;
import kc875.utils.Maybe;

import java.util.List;

public class ClassDecl extends ClassXi {
    private List<FuncDecl> methods;

    public ClassDecl(String name,
                     Maybe<String> superClass,
                     List<StmtDecl> fields,
                     List<FuncDecl> methods,
                     ComplexSymbolFactory.Location location) {
        super(name, superClass, fields, location);
        this.methods = methods;
    }

    public ClassDecl(String name,
                     String superClass,
                     List<StmtDecl> fields,
                     List<FuncDecl> methods,
                     ComplexSymbolFactory.Location location) {
        super(name, superClass, fields, location);
        this.methods = methods;
    }

    public ClassDecl(String name,
                     List<StmtDecl> fields,
                     List<FuncDecl> methods,
                     ComplexSymbolFactory.Location location) {
        super(name, fields, location);
        this.methods = methods;
    }

    public List<FuncDecl> getMethods() {
        return methods;
    }

    @Override
    public void accept(TypeCheckVisitor visitor) {
        // do nothing
    }

    @Override
    public IRNode accept(IRTranslationVisitor visitor) {
        return visitor.visit(this);
    }

    @Override
    public void prettyPrint(CodeWriterSExpPrinter w) {
        w.startList();
        w.printAtom(name + superClass.to(sc -> " extends " + sc).otherwise(""));
        w.startList();
        fields.forEach(f -> f.prettyPrint(w));
        w.endList();
        w.startList();
        methods.forEach(m -> m.prettyPrint(w));
        w.endList();
        w.endList();
    }
}
