package kc875.ast;

import edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;
import edu.cornell.cs.cs4120.xic.ir.IRNode;
import java_cup.runtime.ComplexSymbolFactory;
import kc875.ast.visit.IRTranslationVisitor;
import kc875.ast.visit.TypeCheckVisitor;
import kc875.utils.Maybe;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.HashSet;

public class ClassDefn extends ASTNode implements Printable, TopLevelDecl {
    private String name;
    private Maybe<String> superClass;
    private List<StmtDecl> fields;
    private List<FuncDefn> methods;

    private Set<String> fieldNames;
    private Set<String> methodNames;

    public ClassDefn(String name,
                     List<StmtDecl> fields,
                     List<FuncDefn> methods,
                     ComplexSymbolFactory.Location location
    ) {
        super(location);
        this.name = name;
        this.superClass = Maybe.unknown();
        this.fields = fields;
        this.methods = methods;

        this.fieldNames = new HashSet<>();
        for (StmtDecl f : fields) {
            if (f instanceof StmtDeclSingle) {
                fieldNames.add(((StmtDeclSingle) f).getName());
            } else if (f instanceof StmtDeclMulti) {
                fieldNames.addAll(((StmtDeclMulti) f).getVars());
            }
        }

        this.methodNames = methods.stream()
                .map(FuncDefn::getName)
                .collect(Collectors.toSet());
    }

    public ClassDefn(String name,
                     String superClass,
                     List<StmtDecl> fields,
                     List<FuncDefn> methods,
                     ComplexSymbolFactory.Location location
    ) {
        super(location);
        this.name = name;
        this.superClass = Maybe.definitely(superClass);
        this.fields = fields;
        this.methods = methods;

        this.fieldNames = new HashSet<>();
        for (StmtDecl f : fields) {
            if (f instanceof StmtDeclSingle) {
                fieldNames.add(((StmtDeclSingle) f).getName());
            } else if (f instanceof StmtDeclMulti) {
                fieldNames.addAll(((StmtDeclMulti) f).getVars());
            }
        }

        this.methodNames = methods.stream()
                .map(FuncDefn::getName)
                .collect(Collectors.toSet());
    }

    public String getName() {
        return name;
    }

    public Maybe<String> getSuperClass() {
        return superClass;
    }

    public List<StmtDecl> getFields() {
        return fields;
    }

    public List<FuncDefn> getMethods() {
        return methods;
    }

    public Set<String> getFieldNames() {
        return fieldNames;
    }

    public Set<String> getMethodNames() {
        return methodNames;
    }

    public boolean hasField(String name) {
        return fieldNames.contains(name);
    }

    public boolean hasMethod(String name) {
        return methodNames.contains(name);
    }

    @Override
    public void accept(TypeCheckVisitor visitor) {
        visitor.visit(this);
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

    /**
     * Create a corresponding class declaration for this definition.
     *
     * @return The declaration.
     */
    public ClassDecl toDecl() {
        List<FuncDecl> methodDecls = methods.stream()
                .map(FuncDefn::toDecl)
                .collect(Collectors.toList());
        return new ClassDecl(name, superClass, fields, methodDecls, getLocation());
    }
}
