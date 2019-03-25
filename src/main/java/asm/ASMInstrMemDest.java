package asm;

public class ASMInstrMemDest extends ASMInstr {
    ASMMem dest;
    ASMExpr src;

    ASMInstrMemDest(ASMOpCode opCode, ASMMem m, ASMConst imm){
        this.opCode = opCode;
        this.dest = m;
        this.src = imm;
    }

    ASMInstrMemDest(ASMOpCode opCode, ASMMem m, ASMReg r){
        this.opCode = opCode;
        this.dest = m;
        this.src = r;
    }

    @Override
    public String toString() {
        return formatOpCode() + " "+ dest.toString() + ", " + src.toString();
    }
}
