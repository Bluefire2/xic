package ast;

import java_cup.runtime.ComplexSymbolFactory;
import lexer.XiTokenLocation;
import edu.cornell.cs.cs4120.xic.ir.*;

public abstract class ASTNode {
    private XiTokenLocation location;

    ASTNode(ComplexSymbolFactory.Location location) {
        this.location = (XiTokenLocation) location;
    }

    public abstract void accept(VisitorTypeCheck visitor);

    public abstract IRNode accept(VisitorTranslation visitor);

    public XiTokenLocation getLocation() {
        return location;
    }
}
