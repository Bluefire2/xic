package kc875.ast;

import java_cup.runtime.ComplexSymbolFactory;

//top level "nodes"
public abstract class FileSource extends ASTNode implements Printable {
    public abstract boolean isInterface();

    public FileSource(ComplexSymbolFactory.Location location) {
        super(location);
    }
}
