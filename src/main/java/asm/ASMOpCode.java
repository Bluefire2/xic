package asm;

import edu.cornell.cs.cs4120.util.InternalCompilerError;
import edu.cornell.cs.cs4120.xic.ir.IRBinOp;

public enum ASMOpCode {

    //Arithmetic
    ADD,
    SUB,
    MUL,
    DIV,
    IMUL,
    IDIV,
    NEG,
    ADDC,
    SBB,
    INC,
    DEC,
    PMULHW, //for hi-mult

    //Logical
    AND,
    OR,
    XOR,
    NOT,

    //Shift and rotate
    SHR,
    SHL,
    SAR,
    SAL,
    SHLD,
    SHRD,
    ROR,
    ROL,
    RCR,
    RCL,

    //Data transfer
    MOV,
    XCHG,
    CMPXCHG,
    MOVZ,
    MOVZX,
    MOVS,
    MOVSX,
    MOVSB,
    MOVSW,
    LEA,
    LDS,
    PUSH,
    POP,
    PUSHA,
    POPA,
    PUSHF,
    POPF,
    IN,
    OUT,

    //Control flow
    LABEL,
    TEST,
    CMP,
    CALL,
    RET,

    JMP,
    JE,
    JNE,
    JG,
    JGE,
    JA,
    JAE,
    JL,
    JLE,
    JB,
    JBE,
    JO,
    JNO,
    JZ,
    JNZ,
    JS,
    JNS,

    SETE,
    SETNE,
    SETG,
    SETGE,
    SETA,
    SETAE,
    SETL,
    SETLE,
    SETB,
    SETBE,
    SETO,
    SETNO,
    SETZ,
    SETNZ,
    SETS,
    SETNS,

    LOOP,
    LOOPE,
    LOOPNE,
    LOOPZ,
    LOOPNZ,

    ENTER,
    LEAVE,

    HLT,
    LOCK,
    NOP,
    WAIT;

    @Override
    public String toString() {
        return this.name().toLowerCase();
    }

    /**
     * Returns the ASMOpCode of input IR binary operation. Since logical
     * binops don't have a direct binop in assembly, the function throws an
     * InternalCompilerError for them.
     *
     * @param op to translate.
     * @return the corresponding assembly binop code.
     */
    public static ASMOpCode asmOpCodeOf(IRBinOp.OpType op) {
        //comparison operators not translatable
        switch (op) {
            case ADD:
                return ASMOpCode.ADD;
            case SUB:
                return ASMOpCode.SUB;
            case MUL:
                return ASMOpCode.IMUL;
            case HMUL:
                return ASMOpCode.PMULHW;
            case DIV:
                return ASMOpCode.IDIV;
            case MOD:
                return ASMOpCode.DIV;
            case AND:
                return ASMOpCode.AND;
            case OR:
                return ASMOpCode.OR;
            case XOR:
                return ASMOpCode.XOR;
            case LSHIFT:
                return ASMOpCode.SHL;
            case RSHIFT:
                return ASMOpCode.SHR;
            case ARSHIFT:
                return ASMOpCode.SAR;
            default:
                throw new InternalCompilerError("Cannot translate op type");
        }
    }

    /**
     * Returns the ASMOpCode for jcc of input IR logical operation. Since
     * arith binops don't have a jcc in assembly, the function throws an
     * InternalCompilerError for them.
     *
     * @param op to translate.
     * @return the corresponding assembly jcc code.
     */
    public static ASMOpCode jmpASMOpCodeOf(IRBinOp.OpType op) {
        switch (op) {
            case EQ:
                return ASMOpCode.JE;
            case NEQ:
                return ASMOpCode.JNE;
            case LT:
                return ASMOpCode.JL;
            case GT:
                return ASMOpCode.JG;
            case LEQ:
                return ASMOpCode.JLE;
            case GEQ:
                return ASMOpCode.JGE;
            default:
                throw new InternalCompilerError("Cannot translate op type");
        }
    }

    /**
     * Returns the ASMOpCode for setcc of input IR logical operation. Since
     * arith binops don't have a setcc in assembly, the function throws an
     * InternalCompilerError for them.
     *
     * @param op to translate.
     * @return the corresponding assembly setcc code.
     */
    public static ASMOpCode setASMOpCodeOf(IRBinOp.OpType op) {
        switch (op) {
            case EQ:
                return ASMOpCode.SETE;
            case NEQ:
                return ASMOpCode.SETNE;
            case LT:
                return ASMOpCode.SETL;
            case GT:
                return ASMOpCode.SETG;
            case LEQ:
                return ASMOpCode.SETLE;
            case GEQ:
                return ASMOpCode.SETGE;
            default:
                throw new InternalCompilerError("Cannot translate op type");
        }
    }

}
