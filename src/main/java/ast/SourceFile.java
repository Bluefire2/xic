package ast;

//top level "nodes"
public abstract class SourceFile implements Printable, ASTNode {
    abstract boolean isInterface();
}
