package kc875.ast;

public abstract class TypeT implements Printable {
    //abstract boolean sameType(TypeT t) throws TypeCheckException;
    public abstract boolean subtypeOf(TypeT t);
}
