package kc875.ast;

import edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;
import edu.cornell.cs.cs4120.xic.ir.IRNode;
import java_cup.runtime.ComplexSymbolFactory;
import kc875.ast.visit.IRTranslationVisitor;
import kc875.ast.visit.TypeCheckVisitor;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ClassDefn extends ASTNode implements Printable, DeclOrDefn {
    private String name;
    private String superClass;
    private List<StmtDecl> fields;
    private List<StmtDeclAssign> initializedFields; // TODO: are these allowed?
    private List<FuncDefn> methods;

    private Set<String> fieldNames;
    private Set<String> methodNames;

    public ClassDefn(ComplexSymbolFactory.Location location,
                     String name,
                     String superClass,
                     List<StmtDecl> fields,
                     List<StmtDeclAssign> initializedFields,
                     List<FuncDefn> methods) {
        super(location);
        this.name = name;
        this.superClass = superClass;
        this.fields = fields;
        this.initializedFields = initializedFields;
        this.methods = methods;

        this.fieldNames = fields.stream()
                .map(StmtDecl::getName)
                .collect(Collectors.toSet());
        initializedFields.forEach(field -> this.fieldNames.addAll(field.getNames()));

        this.methodNames = methods.stream()
                .map(FuncDefn::getName)
                .collect(Collectors.toSet());
    }

    public String getName() {
        return name;
    }

    public String getSuperClass() {
        return superClass;
    }

    public List<StmtDecl> getFields() {
        return fields;
    }

    public List<StmtDeclAssign> getInitializedFields() {
        return initializedFields;
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
        // TODO
    }
}
