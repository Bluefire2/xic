package kc875.ast;

import java_cup.runtime.ComplexSymbolFactory;

/**
 * A FileSource AST for a .xi or .ixi file.
 */
public abstract class FileSource extends ASTNode implements Printable {
    public abstract boolean isInterface();

    FileSource(ComplexSymbolFactory.Location location) {
        super(location);
    }
}
