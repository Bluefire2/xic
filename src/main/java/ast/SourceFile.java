package ast;

//top level "nodes"
public abstract class SourceFile implements Printable, TypeCheckable {
    abstract boolean isInterface();
}
