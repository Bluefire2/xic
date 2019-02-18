package ast;

import edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;

import java.util.ArrayList;
import java.util.List;

public class ProgramFile extends SourceFile {
    private List<UseInterface> imports;
    private List<FuncDefn> funcDefns;

    public ProgramFile(List<UseInterface> imports, List<FuncDefn> funcDefns) {
        this.imports = new ArrayList<>(imports);
        this.funcDefns = new ArrayList<>(funcDefns);
    }

    public List<UseInterface> getImports() {
        return imports;
    }

    public List<FuncDefn> getFuncDefns() {
        return funcDefns;
    }

    public void addFuncDefn(FuncDefn funcDefn) {
        funcDefns.add(funcDefn);
    }

    public void addImport(UseInterface use) {
        imports.add(use);
    }

    public boolean isInterface() {
        return false;
    }

    public void prettyPrint(CodeWriterSExpPrinter w) {
        w.startUnifiedList();
        w.startUnifiedList();
        imports.forEach((i) -> i.prettyPrint(w));
        w.endList();
        w.startUnifiedList();
        funcDefns.forEach((d) -> d.prettyPrint(w));
        w.endList();
        w.endList();
    }

    @Override
    public void accept(VisitorAST visitor) {
        //TODO
    }
}
