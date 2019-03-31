package edu.cornell.cs.cs4120.xic.ir.visit;

import asm.*;
import edu.cornell.cs.cs4120.util.InternalCompilerError;
import edu.cornell.cs.cs4120.xic.ir.*;
import edu.cornell.cs.cs4120.xic.ir.IRBinOp.OpType;

import java.util.ArrayList;
import java.util.List;
import polyglot.util.Pair;
import java.util.function.Function;

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

    //return if IRConst has 1,2,4,8
    private boolean validAddrScale(IRConst i) {
        return i.value() == 1
                || i.value() == 2
                || i.value() == 4
                || i.value() == 8;
    }

    //return if IRConst fits in 32 bits
    private boolean validAddrDispl(IRConst i){
        return i.value() == (long)((int)i.value());
    }

    private Pair<List<ASMInstr>,ASMExprMem> tileMemMult(IRBinOp exp) {
        IRExpr l = exp.left();
        IRExpr r = exp.right();
        if (l instanceof IRConst && validAddrScale((IRConst) l)) {
            if (r instanceof IRTemp) { //[C * T] => [T * C]
                return new Pair<>(
                        new ArrayList<>(),
                        new ASMExprMem(
                                new ASMExprBinOpMult(
                                        new ASMExprTemp(((IRTemp) r).name()),
                                        new ASMExprConst(((IRConst) l).value())
                                )
                        )
                );
            } else {//[C * non-temp] => [t0 * C]
                String t0 = newTemp();
                return new Pair<>(
                        r.accept(this, new ASMExprTemp(t0)),
                        new ASMExprMem(
                                new ASMExprBinOpMult(
                                        new ASMExprTemp(t0),
                                        new ASMExprConst(((IRConst) l).value())
                                )
                        )
                );
            }
        } else if (r instanceof IRConst && validAddrScale((IRConst) r)) {
            if (l instanceof IRTemp) { //[T * C]
                return new Pair<>(
                        new ArrayList<>(),
                        new ASMExprMem(
                                new ASMExprBinOpMult(
                                        new ASMExprTemp(((IRTemp) l).name()),
                                        new ASMExprConst(((IRConst) r).value())
                                )
                        )
                );
            } else {//[non-temp * C] => [t0 * C]
                String t0 = newTemp();
                return new Pair<>(
                        l.accept(this, new ASMExprTemp(t0)),
                        new ASMExprMem(
                                new ASMExprBinOpMult(
                                        new ASMExprTemp(t0),
                                        new ASMExprConst(((IRConst) r).value())
                                )
                        )
                );
            }
        } else { //[non-const * non-const]
            String t0 = newTemp();
            return new Pair<>(
                    exp.accept(this, new ASMExprTemp(t0)),
                    new ASMExprMem(new ASMExprTemp(t0))
            );
        }
    }

    private List<IRExpr> flattenAdds(IRBinOp b){
        List<IRExpr> exps = new ArrayList<>();
        if (b.opType() == OpType.ADD) {
            if (b.left() instanceof IRBinOp && ((IRBinOp) b.left()).opType() == OpType.ADD) {
                exps.addAll(flattenAdds((IRBinOp) b.left()));
            } else {
                exps.add(b.left());
            }
            if (b.right() instanceof IRBinOp && ((IRBinOp) b.right()).opType() == OpType.ADD) {
                exps.addAll(flattenAdds((IRBinOp) b.right()));
            } else {
                exps.add(b.right());
            }
        } else {
            exps.add(b);
        }
        return exps;
    }


    //tile inside a mem expr
    //[base + index * scale + displacement]
    //base and index are temps
    //scale is 1/2/4/8, displacement is 32 bits
    public Pair<List<ASMInstr>,ASMExprMem> tileMemExpr(IRMem m) {
        IRExpr e = m.expr();
        Pair<List<ASMInstr>, ASMExprMem> res = e.matchLow(
                (IRBinOp exp) -> {
                    if (exp.opType() == OpType.ADD) {
                        List<ASMInstr> instrs = new ArrayList<>();
                        List<IRExpr> flattened = flattenAdds(exp);
                        ASMExprTemp base = null;
                        ASMExpr index_scale = null;
                        ASMExprConst offset = null;
                        //O
                        for (int i = 0; i < flattened.size(); i ++) {
                            IRExpr curr = flattened.get(i);
                            if (curr instanceof IRConst && validAddrDispl((IRConst) curr)) {
                                offset = new ASMExprConst(((IRConst) curr).value());
                                flattened.remove(i);
                                break;
                            }
                        }
                        //I * S
                        for (int i = 0; i < flattened.size(); i ++) {
                            IRExpr curr = flattened.get(i);
                            if (curr instanceof IRBinOp && ((IRBinOp) curr).opType() == OpType.MUL) {
                                Pair<List<ASMInstr>, ASMExprMem> I_S = tileMemMult((IRBinOp) curr);
                                instrs.addAll(I_S.part1());
                                index_scale = I_S.part2().getAddr();
                                flattened.remove(i);
                                break;
                            }
                        }
                        if (index_scale == null) {//I with no S
                            for (int i = 0; i < flattened.size(); i ++) {
                                IRExpr curr = flattened.get(i);
                                if (curr instanceof IRTemp) {
                                    index_scale = new ASMExprTemp(((IRTemp) curr).name());
                                    flattened.remove(i);
                                    break;
                                }
                            }
                        }
                        //B
                        if (flattened.size() != 0){
                            IRExpr remaining = flattened.stream()
                                    .reduce((a, b)
                                            -> new IRBinOp(OpType.ADD, a, b)).get();
                            if (remaining instanceof IRTemp) {//only one temp left
                                base = new ASMExprTemp(((IRTemp) remaining).name());
                            } else { //bunch of temps left, need to calculate
                                String t0 = newTemp();
                                instrs.addAll(
                                        remaining.accept(this, new ASMExprTemp(t0))
                                );
                                base = new ASMExprTemp(t0);
                            }
                        }
                        ASMExpr memExpr = base;
                        if (index_scale != null) {
                            memExpr = (memExpr == null) ?
                                    index_scale : new ASMExprBinOpAdd(memExpr, index_scale);
                        }
                        if (offset != null) {
                            memExpr = (memExpr == null) ?
                                    offset : new ASMExprBinOpAdd(memExpr, offset);
                        }
                        if (memExpr == null) {
                            throw new IllegalAccessError("what the FUCK");
                        }
                        return new Pair<>(instrs, new ASMExprMem(memExpr));
                    } else if (exp.opType() == OpType.MUL) {
                        return tileMemMult(exp);
                    } else {
                        String t0 = newTemp();
                        return new Pair<>(
                                exp.accept(this, new ASMExprTemp(t0)),
                                new ASMExprMem(new ASMExprTemp(t0))
                        );
                    }
                },
                (IRCall exp) -> {
                    String t0 = newTemp();
                    return new Pair<>(
                            exp.accept(this, new ASMExprTemp(t0)),
                            new ASMExprMem(new ASMExprTemp(t0))
                    );
                },
                (IRConst exp) -> {
                    String t0 = newTemp();
                    return new Pair<>(
                            exp.accept(this, new ASMExprTemp(t0)),
                            new ASMExprMem(new ASMExprTemp(t0))
                    );
                },
                (IRMem exp) -> {
                    String t0 = newTemp();
                    return new Pair<>(
                            exp.accept(this, new ASMExprTemp(t0)),
                            new ASMExprMem(new ASMExprTemp(t0))
                    );
                },
                (IRName exp) -> {
                    throw new IllegalAccessError();
                },
                (IRTemp exp) -> new Pair<>(
                        new ArrayList<>(),
                        new ASMExprMem(new ASMExprTemp(exp.name())))
        );
        return res;
    }

    private <T extends IRExpr> Function<T, Void> addAllAccept(ASMExprTemp dest,
                                                List<ASMInstr> instrs) {
        return (T e) -> {
            instrs.addAll(e.accept(this, dest));
            return null;
        };
    }

    private Function<IRMem, Void> addAllAcceptMem(ASMExprTemp dest,
                                                  List<ASMInstr> instrs) {
        return (IRMem m) -> {
            Pair<List<ASMInstr>,ASMExprMem> memTile = tileMemExpr(m);
                instrs.addAll(memTile.part1());
                instrs.add(new ASMInstr_2Arg(
                        ASMOpCode.MOV,
                        dest,
                        memTile.part2()
                ));
            return null;
        };
    }

    private <T extends IRExpr> Function<T, Void> illegalAccess() {
        return (T e) -> {
            throw new IllegalAccessError();
        };
    }

    public List<ASMInstr> visit(IRBinOp node, ASMExprTemp dest) {
        List<ASMInstr> instrs = new ArrayList<>();
        switch (node.opType()) {
            case ADD:
            case SUB:
            case MUL:
            case LSHIFT:
            case RSHIFT:
            case ARSHIFT:
                /*
                 * Boolean binops can also be tiled this way: we can use x86
                 * bitwise instructions because booleans are all 0/1 anyway.
                 */
            case AND:
            case OR:
            case XOR:
                // For ADD and MUL, switching left and right children might
                // seem to improve performance, but there is no point since
                // one of the children will need to be moved to dest anyway
                // after both are computed. So, lhs and rhs computation can
                // be separated.

                // Visit left child and add the relevant moving ASMs.
                node.left().matchLow(
                        // no, this can't be extracted into a variable
                        addAllAccept(dest, instrs),
                        addAllAccept(dest, instrs),
                        addAllAccept(dest, instrs),
                        addAllAcceptMem(dest, instrs),
                        illegalAccess(),
                        addAllAccept(dest, instrs)
                );

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
                            Pair<List<ASMInstr>,ASMExprMem> memTile = tileMemExpr(r);
                            instrs.addAll(memTile.part1());
                            instrs.add(new ASMInstr_2Arg(
                                    asmOpCodeOf(node.opType()),
                                    dest,
                                    memTile.part2()
                            ));
                            return null;
                        },
                        illegalAccess(),
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
        instrs.add(new ASMInstr_1Arg(ASMOpCode.PUSH, new ASMExprReg("ebp")));
        instrs.add(new ASMInstr_2Arg(ASMOpCode.MOV, new ASMExprReg("ebp"),
                new ASMExprReg("esp")));
        //set up stack frame for local vars?

        //Body
        //Args passed in rdi,rsi,rdx,rcx,r8,r9, (stack in reverse order)
        //If rbx,rbp, r12, r13, r14, r15 used, restore before returning
        //First return in rax, second in rdx

        //Epilogue
        instrs.add(new ASMInstr_2Arg(ASMOpCode.MOV, new ASMExprReg("esp"),
                new ASMExprReg("ebp")));
        instrs.add(new ASMInstr_1Arg(ASMOpCode.POP, new ASMExprReg("ebp")));
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
        Pair<List<ASMInstr>,ASMExprMem> memTile = tileMemExpr(node);
        instrs.addAll(memTile.part1());
        instrs.add(new ASMInstr_2Arg(
                ASMOpCode.MOV,
                dest,
                memTile.part2()
        ));
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
