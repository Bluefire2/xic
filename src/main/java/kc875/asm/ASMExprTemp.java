package kc875.asm;

public class ASMExprTemp extends ASMExprRT {
    private String name;

    public ASMExprTemp(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ASMExprTemp) {
            return this.name.equals(((ASMExprTemp) obj).name);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return ("temp_"+name).hashCode();
    }
}
