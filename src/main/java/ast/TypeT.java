package ast;

public abstract class TypeT implements Printable {
    //abstract boolean sameType(TypeT t) throws TypeCheckException;
    abstract boolean subtypeOf(TypeT t);
}
