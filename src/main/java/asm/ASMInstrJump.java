package asm;

public class ASMInstrJump extends ASMInstr {
    String name;

    public ASMInstrJump(ASMOpCode opCode, String name) {
        this.name = name;
        this.opCode = opCode;
    }

    @Override
    public String toString() {
        return formatOpCode() + " " + name;
    }
}
