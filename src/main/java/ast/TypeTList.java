package ast;
import edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;

import java.util.List;

public class TypeTList extends TypeT {
    private List<TypeTTau> tTauList;

    public TypeTList(List<TypeTTau> tTauList) {
        if (tTauList.size() < 2)
            throw new IllegalArgumentException("Length of T List is not " +
                    "greater than equal to 2");
        this.tTauList = tTauList;
    }

    public List<TypeTTau> getTTauList() {
        return tTauList;
    }

    public int getLength(){
        return tTauList.size();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof TypeTList){
            TypeTList o = (TypeTList) obj;

            // Check the lengths are equal
            int thisLen = getLength();
            if (thisLen != o.getLength())
                return false;

            // Check each tau is equal
            List<TypeTTau> oTTauList = o.getTTauList();
            for (int i = 0; i < thisLen; ++i) {
                if (!tTauList.get(i).equals(oTTauList.get(i)))
                    return false;
            }

            // All taus are equal
            return true;
        }
        return false;
    }

    @Override
    boolean subtypeOf(TypeT t) {
        throw new Error("not implemented: subtyping tau");
    }

    @Override
    public void prettyPrint(CodeWriterSExpPrinter w) {
        tTauList.forEach(t -> t.prettyPrint(w));
    }
}
