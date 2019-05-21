package kc875.asm;

import edu.cornell.cs.cs4120.util.InternalCompilerError;
import edu.cornell.cs.cs4120.xic.ir.IRBinOp;

public enum ASMOpCode {

    //Arithmetic
    ADD,
    SUB,
    IMUL,
    IDIV,
    INC,
    DEC,

    //Logical
    AND,
    OR,
    XOR,
    NOT,

    //Data transfer
    MOV,
    MOVABS,
    MOVZX,
    CQO,
    PUSH,
    POP,

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
    JL,
    JLE,
    JAE,

    SETE,
    SETNE,
    SETG,
    SETGE,
    SETL,
    SETLE,

    ENTER,
    LEAVE,

    //comments
    COMMENT,

    //. (for directives)
    DOT,

    LEA;

    @Override
    public String toString() {
        return this == DOT ? "." : this.name().toLowerCase();
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
            case HMUL:
                return ASMOpCode.IMUL;
            case DIV:
            case MOD:
                return ASMOpCode.IDIV;
            case AND:
                return ASMOpCode.AND;
            case OR:
                return ASMOpCode.OR;
            case XOR:
                return ASMOpCode.XOR;
            case LSHIFT:
            case RSHIFT:
            case ARSHIFT:
                throw new InternalCompilerError(op + " not supported");
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
