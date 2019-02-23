package ast;

public abstract class TypeTTau extends TypeT {
    @Override
    boolean subtypeOf(TypeT t) {
        throw new Error("not implemented: subtyping tau");
    }
}
