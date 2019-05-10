package kc875.ast;

import java_cup.runtime.ComplexSymbolFactory;

public abstract class StmtDecl extends Stmt implements TopLevelDecl {
    public StmtDecl(ComplexSymbolFactory.Location location) {
        super(location);
    }
}
