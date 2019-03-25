package asm;

public class ASMReg extends ASMExpr {
    String name;

    ASMReg(String n) {
        this.name = n;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString(){
        return name;
    }
}
