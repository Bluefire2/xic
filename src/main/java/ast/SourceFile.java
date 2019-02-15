package ast;

import xi_parser.Printable;

//top level "nodes"
public abstract class SourceFile implements Printable {
    abstract boolean isInterface();
}
