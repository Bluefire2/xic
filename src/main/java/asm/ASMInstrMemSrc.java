package asm;

public class ASMInstrMemSrc extends ASMInstr {
    ASMExpr dest;
    ASMMem src;

    ASMInstrMemSrc(ASMOpCode opCode, ASMReg r, ASMMem m){
        this.opCode = opCode;
        this.dest = r;
        this.src = m;
    }

    @Override
    public String toString() {
        return formatOpCode() + " "+ dest.toString() + ", " + src.toString();
    }
}
