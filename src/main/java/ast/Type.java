package ast;

public abstract class Type implements Printable, MetaType {
    abstract boolean subtypeOf(MetaType t);
}
