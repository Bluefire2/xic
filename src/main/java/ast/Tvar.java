package ast;

import edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;

public class Tvar extends Type {
    private String name;

    Tvar(String name) {
        this.name = name;
        this.t_type = TypeType.Tvar;
    }

    public String toString() {
        return name;
    }

    public String getName() {
        return name;
    }

    public void prettyPrint(CodeWriterSExpPrinter w) {
        w.printAtom(this.toString());
    }
}
