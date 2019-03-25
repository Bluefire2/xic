package ast;

import ast.visit.IRTranslationVisitor;
import ast.visit.TypeCheckVisitor;
import java_cup.runtime.ComplexSymbolFactory;
import lexer.XiTokenLocation;
import edu.cornell.cs.cs4120.xic.ir.*;

public abstract class ASTNode {
    private XiTokenLocation location;

    ASTNode(ComplexSymbolFactory.Location location) {
        this.location = (XiTokenLocation) location;
    }

    public abstract void accept(TypeCheckVisitor visitor);

    public abstract IRNode accept(IRTranslationVisitor visitor);

    public XiTokenLocation getLocation() {
        return location;
    }
}
