package ast;

import java.util.List;

public abstract class TypeDecl implements Printable {
    abstract TypeT typeOf();
    abstract List<String> varsOf();
}
