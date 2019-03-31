package edu.cornell.cs.cs4120.xic.ir.visit;

import asm.*;
import edu.cornell.cs.cs4120.util.InternalCompilerError;
import edu.cornell.cs.cs4120.xic.ir.*;
import edu.cornell.cs.cs4120.xic.ir.IRBinOp.OpType;

import java.util.ArrayList;
import java.util.List;

public class ASMTranslationVisitor implements IRBareVisitor<List<ASMInstr>> {
    private int tempcounter;

    private String newTemp() {
        return String.format("_asm_t%d", (tempcounter++));
    }

    public ASMTranslationVisitor() {
        this.tempcounter = 0;
    }


    /**
     * Returns the ASMOpCode of input IR binary operation. Since logical
     * binops don't have a direct binop in assembly, the function throws an
     * InternalCompilerError.
     *
     * @param op to translate.
     * @return the corresponding assembly binop code.
     */
    private ASMOpCode asmOpCodeOf(IRBinOp.OpType op) {
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

    //translating the inside of mem accesses into ASMExpr
    //must check if valid first
    public ASMExpr tileInsideMem(IRExpr e){
        if (e instanceof IRConst){
            return new ASMExprConst(((IRConst) e).value());
        } else if (e instanceof IRTemp) {
            return new ASMExprTemp(((IRTemp) e).name());
        } else if (e instanceof IRBinOp){
            IRBinOp eb = (IRBinOp) e;
            //return if binop node is valid for inside ASM Expr Mem
            //either [a + b] or [a * b + c] or [a * b] where at least one of a or b is a register and the rest are constants
            if (eb.opType() == OpType.ADD){
                if (eb.left() instanceof IRBinOp) { // a * b + c
                    IRBinOp bo = (IRBinOp) eb.left();
                    IRExpr a = bo.left();
                    IRExpr b = bo.right();
                    IRExpr c = eb.right();
                    return new ASMExprBinOpAdd(
                            new ASMExprBinOpMult(
                                    tileInsideMem(a),
                                    tileInsideMem(b)
                            ),
                            tileInsideMem(c)
                    );
                } else if (eb.right() instanceof IRBinOp) { //c + a * b
                    IRBinOp bo = (IRBinOp) eb.right();
                    IRExpr a = bo.left();
                    IRExpr b = bo.right();
                    IRExpr c = eb.left();
                    return new ASMExprBinOpAdd(
                            new ASMExprBinOpMult(
                                    tileInsideMem(a),
                                    tileInsideMem(b)
                            ),
                            tileInsideMem(c)
                    );
                } else {// a + b
                    IRExpr a = eb.left();
                    IRExpr b = eb.right();
                    return new ASMExprBinOpAdd(
                            tileInsideMem(a),
                            tileInsideMem(b)
                    );
                }
            } else if (eb.opType() == OpType.MUL){ // a * b
                IRExpr a = eb.left();
                IRExpr b = eb.right();
                return new ASMExprBinOpMult(
                        tileInsideMem(a),
                        tileInsideMem(b)
                );
            }
        }
        throw new InternalCompilerError("Invalid expression inside mem access");
    }

    public boolean validExprMem(IRMem node){
        IRExpr e = node.expr();
        if (e instanceof IRTemp) {
            return true;
        } else if (e instanceof IRBinOp) {
            IRBinOp eb = (IRBinOp) e;
            //return if binop node is valid for inside ASM Expr Mem
            //either [a + b] or [a * b + c] or [a * b] where at least one of a or b is a register and the rest are constants
            //in a * b exactly one needs to be a register
            if (eb.opType() == OpType.ADD){
                if (eb.left() instanceof IRBinOp) { // a * b + c
                    IRBinOp bo = (IRBinOp) eb.left();
                    if (bo.opType() != OpType.MUL){
                        return false;
                    }
                    IRExpr a = bo.left();
                    IRExpr b = bo.right();
                    IRExpr c = eb.right();
                    if (!(a instanceof IRConst || a instanceof IRTemp)
                            || !((b instanceof IRConst || b instanceof IRTemp))
                            || !((c instanceof IRConst || c instanceof IRTemp))
                    ) {
                        return false;
                    }
                    if (a instanceof IRTemp && b instanceof IRTemp) {
                        return false;
                    }
                    return (a instanceof IRTemp || b instanceof IRTemp);
                } else if (eb.right() instanceof IRBinOp) { //c + a * b
                    IRBinOp bo = (IRBinOp) eb.right();
                    if (bo.opType() != OpType.MUL){
                        return false;
                    }
                    IRExpr a = bo.left();
                    IRExpr b = bo.right();
                    IRExpr c = eb.left();
                    if (!(a instanceof IRConst || a instanceof IRTemp)
                            || !((b instanceof IRConst || b instanceof IRTemp))
                            || !((c instanceof IRConst || c instanceof IRTemp))
                    ) {
                        return false;
                    }
                    if (a instanceof IRTemp && b instanceof IRTemp) {
                        return false;
                    }
                    return (a instanceof IRTemp || b instanceof IRTemp);
                } else {// a + b
                    IRExpr a = eb.left();
                    IRExpr b = eb.right();
                    if (!(a instanceof IRConst || a instanceof IRTemp)
                            || !((b instanceof IRConst || b instanceof IRTemp))
                    ) {
                        return false;
                    }
                    return (a instanceof IRTemp || b instanceof IRTemp);
                }
            } else if (eb.opType() == OpType.MUL){ // a * b
                IRExpr a = eb.left();
                IRExpr b = eb.right();
                if (!(a instanceof IRConst || a instanceof IRTemp)
                        || !((b instanceof IRConst || b instanceof IRTemp))
                ) {
                    return false;
                }
                if (a instanceof IRTemp && b instanceof IRTemp) {
                    return false;
                }
                return (a instanceof IRTemp || b instanceof IRTemp);
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    public List<ASMInstr> visit(IRBinOp node, ASMExprTemp dest) {
        switch (node.opType()) {
            case ADD:
            case SUB:
            case MUL:
                // For ADD and MUL, switching left and right children might
                // seem to improve performance, but there is no point since
                // one of the children will need to be moved to dest anyway
                // after both are computed. So, lhs and rhs computation can
                // be separated.
                List<ASMInstr> instrs = new ArrayList<>();

                // Visit left child and add the relevant moving ASMs.
                node.left().matchLow(
                        (IRBinOp l) -> {
                            instrs.addAll(l.accept(this, dest));
                            return null;
                        },
                        (IRCall l) -> {
                            instrs.addAll(l.accept(this, dest));
                            return null;
                        },
                        (IRConst l) -> {
                            instrs.add(new ASMInstr_2Arg(
                                    // MOV dest, l
                                    ASMOpCode.MOV,
                                    dest,
                                    new ASMExprConst(l.value())
                            ));
                            return null;
                        },
                        (IRMem l) -> {
                            if (validExprMem(l)) {
                                instrs.add(new ASMInstr_2Arg(
                                        ASMOpCode.MOV,
                                        dest,
                                        tileInsideMem(l.expr())
                                ));
                            } else {
                                instrs.addAll(l.accept(this, dest));
                            }
                            return null;
                        },
                        (IRName l) -> {throw new IllegalAccessError();},
                        (IRTemp l) -> {
                            instrs.add(new ASMInstr_2Arg(
                                    // MOV dest, l
                                    ASMOpCode.MOV,
                                    dest,
                                    new ASMExprTemp(l.name())
                            ));
                            return null;
                        });

                // Visit right child and complete adding instructions
                node.right().matchLow(
                        (IRBinOp r) -> {
                            // Store the asm for binop in a new destination
                            // temp, compute operation on the dest (input to
                            // this node) and the new temp
                            String rDest = newTemp();
                            instrs.addAll(
                                    r.accept(this, new ASMExprTemp(rDest))
                            );
                            instrs.add(new ASMInstr_2Arg(
                                    asmOpCodeOf(node.opType()),
                                    dest,
                                    new ASMExprTemp(rDest)
                            ));
                            return null;
                        },
                        (IRCall r) -> {
                            // Store the asm for binop in a new destination
                            // temp, compute operation on the dest (input to
                            // this node) and the new temp
                            String rDest = newTemp();
                            instrs.addAll(
                                    r.accept(this, new ASMExprTemp(rDest))
                            );
                            instrs.add(new ASMInstr_2Arg(
                                    asmOpCodeOf(node.opType()),
                                    dest,
                                    new ASMExprTemp(rDest)
                            ));
                            return null;
                        },
                        (IRConst r) -> {
                            instrs.add(new ASMInstr_2Arg(
                                    // OP dest, r (r is a constant)
                                    asmOpCodeOf(node.opType()),
                                    dest,
                                    new ASMExprConst(r.value())
                            ));
                            return null;
                        },
                        (IRMem r) -> {
                            if (validExprMem(r)) {
                                // Can directly write the [...] expression as
                                // the src of OP dest, src.
                                instrs.add(new ASMInstr_2Arg(
                                        asmOpCodeOf(node.opType()),
                                        dest,
                                        tileInsideMem(r.expr())
                                ));
                            } else {
                                String rDest = newTemp();
                                instrs.addAll(
                                        r.accept(this, new ASMExprTemp(rDest))
                                );
                                instrs.add(new ASMInstr_2Arg(
                                        asmOpCodeOf(node.opType()),
                                        dest,
                                        new ASMExprTemp(rDest)
                                ));
                            }
                            return null;
                        },
                        (IRName r) -> {throw new IllegalAccessError();},
                        (IRTemp r) -> {
                            instrs.add(new ASMInstr_2Arg(
                                    // OP dest, r
                                    asmOpCodeOf(node.opType()),
                                    dest,
                                    new ASMExprTemp(r.name())
                            ));
                            return null;
                        });
                return instrs;
            case HMUL:
                break;
            case DIV:
            case MOD:
                break;
            case AND:
            case OR:
            case XOR:
                break;
            case LSHIFT:
            case RSHIFT:
            case ARSHIFT:
                break;
            case EQ:
            case NEQ:
            case LT:
            case GT:
            case LEQ:
            case GEQ:
                break;
        }
        throw new IllegalAccessError();
    }

    public List<ASMInstr> visit(IRCall node, ASMExprTemp destreg) {
        throw new IllegalAccessError();
    }

    public List<ASMInstr> visit(IRCJump node) {
        throw new IllegalAccessError();
    }

    public List<ASMInstr> visit(IRCompUnit node) {
        throw new IllegalAccessError();
    }

    public List<ASMInstr> visit(IRConst node, ASMExprTemp dest) {
        //c => MOV dest c
        List<ASMInstr> instrs = new ArrayList<>();
        instrs.add(new ASMInstr_2Arg(
                ASMOpCode.MOV,
                dest,
                new ASMExprConst(node.value())
        ));
        return instrs;
    }

    public List<ASMInstr> visit(IRExp node) {
        throw new IllegalAccessError();
    }

    public List<ASMInstr> visit(IRFuncDecl node) {
        List<ASMInstr> instrs = new ArrayList<>();
        String fname = node.name();

        //Prologue
        instrs.add(new ASMInstr_1Arg(
                ASMOpCode.PUSH, new ASMExprReg(ASMExprReg.x86_64Reg.rbp)
        ));
        instrs.add(new ASMInstr_2Arg(
                ASMOpCode.MOV,
                new ASMExprReg(ASMExprReg.x86_64Reg.rbp),
                new ASMExprReg(ASMExprReg.x86_64Reg.rsp))
        );
        //set up stack frame for local vars?

        //Body
        //Args passed in rdi,rsi,rdx,rcx,r8,r9, (stack in reverse order)
        //If rbx,rbp, r12, r13, r14, r15 used, restore before returning
        //First return in rax, second in rdx

        //Epilogue
        instrs.add(new ASMInstr_2Arg(
                ASMOpCode.MOV,
                new ASMExprReg(ASMExprReg.x86_64Reg.rsp),
                new ASMExprReg(ASMExprReg.x86_64Reg.rbp))
        );
        instrs.add(new ASMInstr_1Arg(
                ASMOpCode.POP, new ASMExprReg(ASMExprReg.x86_64Reg.rbp))
        );
        instrs.add(new ASMInstr_0Arg(ASMOpCode.RET));

        return instrs;
    }

    public List<ASMInstr> visit(IRJump node) {
        List<ASMInstr> instrs = new ArrayList<>();
        //JUMP l => JMP l
        if (node.target() instanceof IRName) {
            instrs.add(new ASMInstr_1Arg(
                    ASMOpCode.JMP,
                    new ASMExprName(((IRName) node.target()).name())
            ));
            return instrs;
        } else {
            throw new IllegalAccessError();
        }
    }

    public List<ASMInstr> visit(IRLabel node) {
        List<ASMInstr> instrs = new ArrayList<>();
        instrs.add(new ASMInstrLabel(node.name()));
        return instrs;
    }

    public List<ASMInstr> visit(IRMem node, ASMExprTemp dest) {
        //translation of [e] will put actual contents of [e] into dest
        //this CANNOT be used for writes
        List<ASMInstr> instrs = new ArrayList<>();
        if (validExprMem(node)){
            instrs.add(new ASMInstr_2Arg(
                    ASMOpCode.MOV,
                    dest,
                    tileInsideMem(node.expr())
            ));
        } else {
            String t0 = newTemp();
            instrs.addAll(node.expr().accept(this, new ASMExprTemp(t0)));
            instrs.add(new ASMInstr_2Arg(
                    ASMOpCode.MOV,
                    dest,
                    new ASMExprMem(new ASMExprTemp(t0))
            ));
        }
        return instrs;
    }

    public List<ASMInstr> visit(IRMove node) {
        throw new IllegalAccessError();
    }

    public List<ASMInstr> visit(IRReturn node) {
        throw new IllegalAccessError();
    }

    public List<ASMInstr> visit(IRSeq node) {
        List<ASMInstr> allInstrs = new ArrayList<>();
        node.stmts().forEach(s -> allInstrs.addAll(s.accept(this)));
        return allInstrs;
    }

    public List<ASMInstr> visit(IRTemp node, ASMExprTemp dest) {
        List<ASMInstr> instrs = new ArrayList<>();
        //r => MOV dest r
        instrs.add(new ASMInstr_2Arg(
                ASMOpCode.MOV,
                dest,
                new ASMExprTemp(node.name())
        ));
        return instrs;
    }
}
