package codegen;

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
