package ast;

import edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;

public class TypeTTauArray extends TypeTTau {
    private TypeTTau typeTTau; //if null, then the list is empty and matches any type
    private Expr size = null;

    public TypeTTauArray(TypeTTau typeTTau) {
        this.typeTTau = typeTTau;
    }

    public TypeTTauArray(TypeTTau typeTTau, Expr size) {
        this.typeTTau = typeTTau;
        this.size = size;
    }

    // TODO: don't need this, I think. Arrays must be either int, bool, or a
    //  tau array
    public TypeTTauArray(){ //For empty lists
    }

    @Override
    public String toString() {
        return typeTTau.toString() + " list";
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

    // TODO: complete this function
    /*
    public boolean subtypeOf(TypeTTau t) {
        return this.equals(t) || t instanceof TypeTUnit;
    }
    */
}