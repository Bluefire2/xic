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


    public ASMOpCode translateBinOpCode(IRBinOp node){
        //comparison operators not translatable
        switch (node.opType()){
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
        }
        throw new InternalCompilerError("Cannot translate op type");
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
                List<ASMInstr> instrs = new ArrayList<>();
                if (node.left() instanceof IRConst && node.right() instanceof IRConst){
                    //const op const SIZE 3
                    instrs.add(new ASMInstrTwoArg(
                            ASMOpCode.MOV,
                            dest,
                            new ASMExprConst(((IRConst)node.left()).value())
                    ));
                    instrs.add(new ASMInstrTwoArg(
                            translateBinOpCode(node),
                            dest,
                            new ASMExprConst(((IRConst)node.right()).value())
                    ));
                    return instrs;
                } else if (node.left() instanceof IRMem) {
                    //TODO
                } else if (node.right() instanceof IRMem) {
                    //TODO
                } else if (node.right() instanceof IRConst){
                    //something op
                    if (node.left() instanceof IRMem && validExprMem((IRMem) node.left())){
                        //[a * b + c] op const //up to SIZE 8
                        IRMem m = (IRMem) node.left();
                        instrs.add(new ASMInstrTwoArg(
                                translateBinOpCode(node),
                                tileInsideMem(m.expr()),
                                new ASMExprConst(((IRConst) node.right()).value())
                        ));
                        return instrs;
                    } else {//anything else pretty much SIZE 2
                        instrs.addAll(node.left().accept(this, dest));
                        instrs.add(new ASMInstrTwoArg(
                                translateBinOpCode(node),
                                dest,
                                new ASMExprConst(((IRConst) node.right()).value())
                        ));
                    }
                } else if (node.right() instanceof IRTemp){
                        //something op d
                        if (node.left() instanceof IRMem && validExprMem((IRMem) node.left())){
                            //[a * b + c] op d //up to SIZE 8
                            IRMem m = (IRMem) node.left();
                            instrs.add(new ASMInstrTwoArg(
                                    translateBinOpCode(node),
                                    tileInsideMem(m.expr()),
                                    new ASMExprTemp(((IRTemp) node.right()).name())
                            ));
                            return instrs;
                        } else {//anything else pretty much SIZE 2
                            instrs.addAll(node.left().accept(this, dest));
                            instrs.add(new ASMInstrTwoArg(
                                    translateBinOpCode(node),
                                    dest,
                                    new ASMExprTemp(((IRTemp) node.right()).name())
                            ));
                        }
                } else if (node.left() instanceof IRTemp){//assumes associativity
                    //d op something
                    if (node.right() instanceof IRMem && validExprMem((IRMem) node.right())){
                        //[a * b + c] op d //up to SIZE 8
                        IRMem m = (IRMem) node.right();
                        instrs.add(new ASMInstrTwoArg(
                                translateBinOpCode(node),
                                tileInsideMem(m.expr()),
                                new ASMExprTemp(((IRTemp) node.left()).name())
                        ));
                        return instrs;
                    } else {//anything else pretty much SIZE 2
                        instrs.addAll(node.right().accept(this, dest));
                        instrs.add(new ASMInstrTwoArg(
                                translateBinOpCode(node),
                                dest,
                                new ASMExprTemp(((IRTemp) node.left()).name())
                        ));
                    }
                } else if (node.left() instanceof IRConst) {// assumes associativity
                    //const op something
                    if (node.right() instanceof IRMem && validExprMem((IRMem) node.right())) {
                        //[a * b + c] op const //up to SIZE 8
                        IRMem m = (IRMem) node.right();
                        instrs.add(new ASMInstrTwoArg(
                                translateBinOpCode(node),
                                tileInsideMem(m.expr()),
                                new ASMExprConst(((IRConst) node.left()).value())
                        ));
                        return instrs;
                    } else {//anything else pretty much SIZE 2
                        instrs.addAll(node.right().accept(this, dest));
                        instrs.add(new ASMInstrTwoArg(
                                translateBinOpCode(node),
                                dest,
                                new ASMExprConst(((IRConst) node.left()).value())
                        ));
                    }
                }
                    //c + node
//                } else if (node.opType() == OpType.ADD && node.right().equals(new IRConst(1))){
//                    //node + 1 => INC SIZE 2
//                    instrs.addAll(node.left().accept(this, dest));
//                    instrs.add(new ASMInstrOneArg(ASMOpCode.INC, dest));
//                    return instrs;
//                } else if (node.opType() == OpType.SUB && node.right().equals(new IRConst(1))){
//                    //node - 1 => DEC SIZE 2
//                    instrs.addAll(node.left().accept(this, dest));
//                    instrs.add(new ASMInstrOneArg(ASMOpCode.DEC, dest));
//                    return instrs;
//                } else if (node.opType() == OpType.ADD && node.left().equals(new IRConst(1))){
//                    //1 + node => INC SIZE 2
//                    instrs.addAll(node.right().accept(this, dest));
//                    instrs.add(new ASMInstrOneArg(ASMOpCode.INC, dest));
//                    return instrs;
                else {//default case SIZE 1
                    //TODO should we reuse instances of temps
                    instrs.addAll(node.left().accept(this, dest));
                    String t2 = newTemp();
                    instrs.addAll(node.right().accept(this, new ASMExprTemp(t2)));
                    instrs.add(new ASMInstrTwoArg(
                            translateBinOpCode(node),
                            dest,
                            new ASMExprTemp(t2)));
                    return instrs;
                }
            case MUL:
            case HMUL:
                break;
            case SUB:
                break;
            case DIV:
                break;
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
        instrs.add(new ASMInstrMove(
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
        instrs.add(new ASMInstrOneArg(ASMOpCode.PUSH, new ASMExprReg("ebp")));
        instrs.add(new ASMInstrMove(ASMOpCode.MOV, new ASMExprReg("ebp"),
                new ASMExprReg("esp")));
        //set up stack frame for local vars?

        //Body
        //Args passed in rdi,rsi,rdx,rcx,r8,r9, (stack in reverse order)
        //If rbx,rbp, r12, r13, r14, r15 used, restore before returning
        //First return in rax, second in rdx

        //Epilogue
        instrs.add(new ASMInstrMove(ASMOpCode.MOV, new ASMExprReg("esp"),
                new ASMExprReg("ebp")));
        instrs.add(new ASMInstrOneArg(ASMOpCode.POP, new ASMExprReg("ebp")));
        instrs.add(new ASMInstrNoArgs(ASMOpCode.RET));

        return instrs;
    }

    public List<ASMInstr> visit(IRJump node) {
        List<ASMInstr> instrs = new ArrayList<>();
        //JUMP l => JMP l
        if (node.target() instanceof IRName) {
            instrs.add(new ASMInstrJump(
                    ASMOpCode.JMP,
                    ((IRName) node.target()).name()
            ));
            return instrs;
        } else {
            throw new IllegalAccessError();
        }
    }

    public List<ASMInstr> visit(IRLabel node) {
        List<ASMInstr> instrs = new ArrayList<>();
        instrs.add(new ASMInstrLabel(
                node.name()
        ));
        return instrs;
    }

    public List<ASMInstr> visit(IRMem node, ASMExprTemp dest) {
        throw new IllegalAccessError();
    }

    public List<ASMInstr> visit(IRMove node) {
        throw new IllegalAccessError();
    }

    public List<ASMInstr> visit(IRReturn node) {
        throw new IllegalAccessError();
    }

    public List<ASMInstr> visit(IRSeq node) {
        throw new IllegalAccessError();
    }

    public List<ASMInstr> visit(IRTemp node, ASMExprTemp dest) {
        List<ASMInstr> instrs = new ArrayList<>();
        //r => MOV dest r
        instrs.add(new ASMInstrMove(
                ASMOpCode.MOV,
                dest,
                new ASMExprTemp(node.name())
        ));
        return instrs;
    }
}
