package kc875.ast;
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
    public String toString() {
        String s = "(" + tTauList.get(0).toString();
        for (int i = 1; i < tTauList.size(); i++) {
            s = s + "," + tTauList.get(i).toString();
        }
        return s + ")";
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof TypeTList){
            List<TypeTTau> otherTauList = ((TypeTList) obj).getTTauList();

            // Check the lengths are equal
            int otherLen = otherTauList.size();
            if (tTauList.size() != otherLen)
                return false;

            // Check each tau is equal
            for (int i = 0; i < otherLen; ++i) {
                if (!tTauList.get(i).equals(otherTauList.get(i)))
                    return false;
            }

            // All taus are equal
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void prettyPrint(CodeWriterSExpPrinter w) {
        tTauList.forEach(t -> t.prettyPrint(w));
    }
}
