package asm;

public class ASMInstrLabel extends ASMInstr {
    private String name;

    public ASMInstrLabel(ASMOpCode opCode, String name) {
        super(opCode);
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
