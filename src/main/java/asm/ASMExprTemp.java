package asm;

public class ASMExprTemp extends ASMExprRegReplaceable {
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
}
