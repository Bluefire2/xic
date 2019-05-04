package kc875.ast;

import edu.cornell.cs.cs4120.xic.ir.IRNode;
import java_cup.runtime.ComplexSymbolFactory;
import kc875.ast.visit.IRTranslationVisitor;
import kc875.ast.visit.TypeCheckVisitor;
import kc875.lexer.XiTokenLocation;

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
