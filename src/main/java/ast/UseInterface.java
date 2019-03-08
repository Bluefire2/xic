package ast;

import edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;
import edu.cornell.cs.cs4120.xic.ir.IRNode;
import java_cup.runtime.ComplexSymbolFactory;

public class UseInterface extends ASTNode {
    private String name;

    public UseInterface(String name, ComplexSymbolFactory.Location location) {
        super(location);
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

    @Override
    public void accept(VisitorTypeCheck visitor) {
        visitor.visit(this);
    }

    @Override
    public IRNode accept(VisitorTranslation visitor) {
        return visitor.visit(this);
    }
}
