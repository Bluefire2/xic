package ast;

public interface MetaType {
    boolean sameType(MetaType t) throws TypeCheckException;
}
