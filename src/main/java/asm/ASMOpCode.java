package asm;

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

    public String formatOpCode(ASMOpCode op) {
        return op.name().toLowerCase();
    }

}


