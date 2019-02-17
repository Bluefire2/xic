package ast;

import edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;

public class ListType extends Type {
    private Type contentsType; //if null, then the list is empty and matches any type
    private Expr length;

    public ListType(Type type) {
        this.contentsType = type;
    }

    public ListType(Type type, Expr length) {
        this.contentsType = type;
        this.length = length;
    }

    public ListType(){ //For empty lists
    }

    public String toString() {
        return contentsType.toString() + " list";
    }

    public Type getContentsType() {
        return contentsType;
    }

    public Expr getLength() {
        return length;
    }

    public void setContentsType(Type contentsType) {
        this.contentsType = contentsType;
    }

    public void prettyPrint(CodeWriterSExpPrinter w) {
        w.startList();
        w.printAtom("[]");
        contentsType.prettyPrint(w);
        if (length != null){
            length.prettyPrint(w);
        }
        w.endList();
    }

    public boolean sameType(MetaType t) {
        if (t instanceof ListType){
            ListType other = (ListType) t;
            if contentsType == null || other.getContentsType() == null ||
                    contentsType.sameType(other.getContentsType());
        }
        return false;
    }

    public boolean subtypeOf(Type t) {
        return this.equals(t) || t instanceof UnitType;
    }
}
