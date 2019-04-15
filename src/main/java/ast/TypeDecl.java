package ast;

import java.util.List;

public abstract class TypeDecl implements Printable {
    public abstract TypeT typeOf();
    public abstract List<String> varsOf();
}
