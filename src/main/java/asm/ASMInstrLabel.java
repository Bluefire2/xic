package asm;

public class ASMInstrLabel extends ASMInstr {
    private String name;

    public ASMInstrLabel(String name) {
        super(null); //TODO get rid of this null
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name + ":";
    }
}
