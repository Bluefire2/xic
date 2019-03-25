package asm;

abstract class ASMInstr {
    ASMOpCode opCode;

    String formatOpCode() {
        //TODO
        return"";
    }

    public ASMOpCode getOpCode() {
        return opCode;
    }
}
