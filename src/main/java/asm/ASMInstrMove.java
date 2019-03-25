package asm;

public class ASMInstrMove extends ASMInstrTwoArg {

    public ASMInstrMove(ASMOpCode opCode, ASMExpr dest, ASMExpr src) {
        super(opCode, dest, src);
    }
}
