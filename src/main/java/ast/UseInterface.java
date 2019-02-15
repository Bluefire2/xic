package ast;

import edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;

public class UseInterface {
    private String name;

    public UseInterface(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void prettyPrint(CodeWriterSExpPrinter w) {
        w.startList();
        w.printAtom("use");
        w.printAtom(name);
        w.endList();
    }
}
