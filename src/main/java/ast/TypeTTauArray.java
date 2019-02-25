package ast;

import edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;

public class TypeTTauArray extends TypeTTau {
    // TODO: The prospect of tau being null might result in
    //  NullPointerExceptions from many functions in this class
    private TypeTTau typeTTau; //if null, then the list is empty and matches any type
    private Expr size = null;

    public TypeTTauArray(TypeTTau typeTTau) {
        this.typeTTau = typeTTau;
    }

    public TypeTTauArray(TypeTTau typeTTau, Expr size) {
        this.typeTTau = typeTTau;
        this.size = size;
    }

    public TypeTTauArray(){ //For empty lists
    }

    @Override
    public String toString() {
        return typeTTau.toString() + "[]";
    }

    public TypeTTau getTypeTTau() {
        return typeTTau;
    }

    public Expr getSize() {
        return size;
    }

    public void setTypeTTau(TypeTTau typeTTau) {
        this.typeTTau = typeTTau;
    }

    public void prettyPrint(CodeWriterSExpPrinter w) {
        w.startList();
        w.printAtom("[]");
        typeTTau.prettyPrint(w);
        if (size != null){
            size.prettyPrint(w);
        }
        w.endList();
    }

    // TODO: complete this function
    /*
    public boolean sameType(TypeT t) {
        if (t instanceof TypeTauArray){
            TypeTauArray other = (TypeTauArray) t;
            if typeTau == null || other.getTypeTau() == null ||
                    typeTau.sameType(other.getTypeTau());
        }
        return false;
    }
    */

    @Override
    public boolean subtypeOf(TypeT t) {
        if (!(t instanceof TypeTTauArray)) {
            return false;
        }
        if (t instanceof TypeTUnit) {
            return true;
        }
        return this.typeTTau.subtypeOf(((TypeTTauArray)t).typeTTau);
    }
}
