package ast;

import java_cup.runtime.ComplexSymbolFactory;
import lexer.XiTokenLocation;

public abstract class ASTNode {
    private XiTokenLocation location;

    ASTNode(ComplexSymbolFactory.Location location) {
        this.location = (XiTokenLocation) location;
    }

    public abstract void accept(VisitorAST visitor);

    XiTokenLocation getLocation() {
        return location;
    }
}
