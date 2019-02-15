package ast;

import edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;

public class ListType extends Type {
    private Type contentsType;
    private Expr length;

    public ListType(Type type) {
        this.contentsType = type;
        this.t_type = TypeType.ListType;
    }

    public ListType(Type type, Expr length) {
        this.contentsType = type;
        this.length = length;
        this.t_type = TypeType.ListType;
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
}
