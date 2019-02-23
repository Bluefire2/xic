package ast;

import java_cup.runtime.ComplexSymbolFactory;

public abstract class Assignable extends ASTNode implements Printable {

    public Assignable(ComplexSymbolFactory.Location location) {
        super(location);
    }
}
