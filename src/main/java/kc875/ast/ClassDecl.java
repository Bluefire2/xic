package kc875.ast;

import edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;
import edu.cornell.cs.cs4120.xic.ir.IRNode;
import java_cup.runtime.ComplexSymbolFactory;
import kc875.ast.visit.IRTranslationVisitor;
import kc875.ast.visit.TypeCheckVisitor;
import kc875.utils.Maybe;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Set;

public class ClassDecl extends ClassXi {
    public ClassDecl(String name,
                     Maybe<String> superClass,
                     List<FuncDecl> methods,
                     ComplexSymbolFactory.Location location) {
        super(name, superClass, new ArrayList<>(), methods, location);
    }

    public ClassDecl(String name,
                     String superClass,
                     List<FuncDecl> methods,
                     ComplexSymbolFactory.Location location) {
        super(name, superClass, new ArrayList<>(), methods, location);
    }

    public ClassDecl(String name,
                     List<FuncDecl> methods,
                     ComplexSymbolFactory.Location location) {
        super(name, new ArrayList<>(), methods, location);
    }

    // Preserved fields for toDecl function in ClassDefn
    public ClassDecl(String name,
              Maybe<String> superClass,
              List<StmtDecl> fields,
              List<FuncDecl> methods,
              ComplexSymbolFactory.Location location) {
        super(name, superClass, fields, methods, location);
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
        methodDecls.forEach(m -> m.prettyPrint(w));
        w.endList();
        w.endList();
    }
}
