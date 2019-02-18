package ast;

import java.util.List;

public abstract class TypeDecl implements Printable {
    abstract TypeT typeOf(TypeDecl t);
    abstract List<String> varsOf(TypeDecl t);
}
