package kc875.ast;

import java_cup.runtime.ComplexSymbolFactory;

import java.util.List;

public abstract class StmtDecl extends Stmt implements TopLevelDecl {
    public StmtDecl(ComplexSymbolFactory.Location location) {
        super(location);
    }

    public abstract List<String> varsOf();
}
