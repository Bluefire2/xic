package codegen;

public class ASMInstrNoMem extends ASMInstr {
    ASMExpr dest;
    ASMExpr src;

    ASMInstrNoMem(ASMOpCode opCode, ASMReg r, ASMConst imm){
        this.opCode = opCode;
        this.dest = r;
        this.src = imm;
    }

    ASMInstrNoMem(ASMOpCode opCode, ASMReg r1, ASMReg r2){
        this.opCode = opCode;
        this.dest = r1;
        this.src = r2;
    }

    @Override
    public String toString() {
        return formatOpCode() + " "+ dest.toString() + ", " + src.toString();
    }
}
