package kc875.ast;

import edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;

public class TypeTTauClass extends TypeTTau {
    String name;
    String extend;

    //TODO store methods and field layouts

    public TypeTTauClass(String name) {
        this.name = name;
    }

    public TypeTTauClass(String name, String extend) {
        this.name = name;
        this.extend = extend;
    }

    @Override
    public boolean subtypeOf(TypeT t) {
        //TODO
        return false;
    }

    @Override
    public void prettyPrint(CodeWriterSExpPrinter w) {
        //TODO
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);//TODO
    }

    @Override
    public String toString() {
        return super.toString();//TODO
    }
}
