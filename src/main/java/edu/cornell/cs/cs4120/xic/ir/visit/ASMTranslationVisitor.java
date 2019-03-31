package edu.cornell.cs.cs4120.xic.ir.visit;

import asm.*;
import edu.cornell.cs.cs4120.util.InternalCompilerError;
import edu.cornell.cs.cs4120.xic.ir.*;
import edu.cornell.cs.cs4120.xic.ir.IRBinOp.OpType;
import polyglot.util.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Function;

//               Functional programming died for this
//                            |~~~~~~~|
//                            | - . - |
//                            |  \_/  |
//                            |       |
//                            |       |
//                            |       |
// |~.\\\_\~~~~~~~~~~~~~~xx~~~         ~~~~~~~~~~~~~~~~~~~~~/_//;~|
// |  \  o \_         ,XXXXX),                         _..-~ o /  |
// |    ~~\  ~-.     XXXXX`)))),                 _.--~~   .-~~~   |
//  ~~~~~~~`\   ~\~~~XXX' _/ ';))     |~~~~~~..-~     _.-~ ~~~~~~~
//           `\   ~~--`_\~\, ;;;\)__.---.~~~      _.-~
//             ~-.       `:;;/;; \          _..-~~
//                ~-._      `''        /-~-~
//                    `\              /  /
//                      |         ,   | |
//                       |  '        /  |
//                        \/;          |
//                         ;;          |
//                         `;   .       |
//                         |~~~-----.....|
//                        | \             \
//                       | /\~~--...__    |
//                       (|  `\       __-\|
//                       ||    \_   /~    |
//                       |)     \~-'      |
//                        |      | \      '
//                        |      |  \    :
//                         \     |  |    |
//                          |    )  (    )
//                           \  /;  /\  |
//                           |    |/   |
//                           |    |   |
//                            \  .'  ||
//                            |  |  | |
//                            (  | |  |
//                            |   \ \ |
//                            || o `.)|
//                            |`\\\\) |
//                            |       |
//                            |       |
//                            |       |

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
     * InternalCompilerError for them.
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

    /**
     * Returns the ASMOpCode for setcc of input IR logical operation. Since
     * arith binops don't have a setcc in assembly, the function throws an
     * InternalCompilerError for them.
     *
     * @param op to translate.
     * @return the corresponding assembly setcc code.
     */
    private ASMOpCode setASMOpCodeOf(IRBinOp.OpType op) {
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

    /**
     * Returns the ASMOpCode for jcc of input IR logical operation. Since
     * arith binops don't have a jcc in assembly, the function throws an
     * InternalCompilerError for them.
     *
     * @param op to translate.
     * @return the corresponding assembly jcc code.
     */
    private ASMOpCode jmpASMOpCodeOf(IRBinOp.OpType op) {
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

    //helper function for tiling inside of mem
    //input a MUL binop that is inside mem
    //output is list of instructions and an ASMExprMem
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

    // flatten add exprs
    // example: (+ (+ a b) (+ c (* d e))) => (a,b,c,(* d e))
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
    private Pair<List<ASMInstr>,ASMExprMem> tileMemExpr(IRMem m) {
        IRExpr e = m.expr();
        return e.matchLow(
                (IRBinOp exp) -> {
                    if (exp.opType() == OpType.ADD) {//[a + b]
                        List<ASMInstr> instrs = new ArrayList<>();
                        //flatten all the + ops into a list
                        List<IRExpr> flattened = flattenAdds(exp);
                        //find B, I, S, and O from the flattened exprs
                        ASMExprTemp base = null;
                        ASMExpr index_scale = null;
                        ASMExprConst offset = null;
                        //O will be a const that is 32 bits
                        //try to find O, and remove if found
                        for (int i = 0; i < flattened.size(); i ++) {
                            IRExpr curr = flattened.get(i);
                            if (curr instanceof IRConst && validAddrDispl((IRConst) curr)) {
                                offset = new ASMExprConst(((IRConst) curr).value());
                                flattened.remove(i);
                                break;
                            }
                        }
                        //first try and find I * S, remove if found
                        //currently matches all MUL regardless if they match the format
                        //will end up with either an ASM mult or an ASM temp
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
                        //if nothing matches I * S pattern then assume S is not provided
                        //try and find some temp I, and remove if found
                        if (index_scale == null) {
                            for (int i = 0; i < flattened.size(); i ++) {
                                IRExpr curr = flattened.get(i);
                                if (curr instanceof IRTemp) {
                                    index_scale = new ASMExprTemp(((IRTemp) curr).name());
                                    flattened.remove(i);
                                    break;
                                }
                            }
                        }
                        //try and find B among remaining exprs
                        //if no remaining temps, then B will be null
                        //if 1 remaining temp, B will be a temp
                        //if 2+ remaining temps, we need additional instructions
                        // to calculate and save results to a temp which B will use
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
                        ASMExpr memExpr = base; //B
                        if (index_scale != null) {
                            memExpr = (memExpr == null) ? //B + (I * S)
                                    index_scale : new ASMExprBinOpAdd(memExpr, index_scale);
                        }
                        if (offset != null) {
                            memExpr = (memExpr == null) ?//(B + (I * S)) + O
                                    offset : new ASMExprBinOpAdd(memExpr, offset);
                        }
                        if (memExpr == null) {
                            //this means B, I, S, O are all null
                            throw new IllegalAccessError("what the FUCK");
                        }
                        return new Pair<>(instrs, new ASMExprMem(memExpr));
                    } else if (exp.opType() == OpType.MUL) {
                        //[a * b]
                        //use helper function
                        return tileMemMult(exp);
                    } else {
                        //[a op b] where op is not + or *
                        //calculate using instructions and put into a temp
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
    }

    private <T extends IRExpr> Function<T, Void> binOpArithAddAllAccept(
            ASMExprTemp dest, List<ASMInstr> instrs) {
        return (T e) -> {
            instrs.addAll(e.accept(this, dest));
            return null;
        };
    }

    private <T extends IRExpr> Function<T, ASMExpr> binOpLogicAddAllAccept(
            ASMExprTemp dest, List<ASMInstr> instrs) {
        return (T e) -> {
            instrs.addAll(e.accept(this, dest));
            return dest;
        };
    }

    /**
     * Converts the IRMem into ASMExprMem by adding the necessary asm code to
     * instrs and returning the resultant ASMExprMem.
     *
     * @param m node to tile.
     * @param instrs instructions to add to.
     */
    private ASMExprMem asmMemTileOf(IRMem m, List<ASMInstr> instrs) {
        Pair<List<ASMInstr>, ASMExprMem> memTile = tileMemExpr(m);
        instrs.addAll(memTile.part1());
        return memTile.part2();
    }

    private Function<IRMem, Void> binOpArithAddAllAcceptMem(
            ASMExprTemp dest, List<ASMInstr> instrs) {
        return (IRMem e) -> {
            instrs.add(new ASMInstr_2Arg(
                    ASMOpCode.MOV,
                    dest,
                    asmMemTileOf(e, instrs)
            ));
            return null;
        };
    }

    private <T extends IRExpr, U> Function<T, U> illegalAccessErrorLambda() {
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
                        binOpArithAddAllAccept(dest, instrs),
                        binOpArithAddAllAccept(dest, instrs),
                        binOpArithAddAllAccept(dest, instrs),
                        binOpArithAddAllAcceptMem(dest, instrs),
                        illegalAccessErrorLambda(),
                        binOpArithAddAllAccept(dest, instrs)
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
                            instrs.add(new ASMInstr_2Arg(
                                    asmOpCodeOf(node.opType()),
                                    dest,
                                    asmMemTileOf(r, instrs)
                            ));
                            return null;
                        },
                        illegalAccessErrorLambda(),
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
                // Visit left child and add the relevant moving ASMs.
                ASMExprTemp leftDestTemp = new ASMExprTemp(newTemp());
                ASMExpr leftDest = node.left().matchLow(
                        // no, this can't be extracted into a variable
                        binOpLogicAddAllAccept(leftDestTemp, instrs),
                        binOpLogicAddAllAccept(leftDestTemp, instrs),
                        binOpLogicAddAllAccept(leftDestTemp, instrs),
                        (IRMem m) -> asmMemTileOf(m, instrs),
                        illegalAccessErrorLambda(),
                        (IRTemp l) -> new ASMExprTemp(l.name())
                );

                ASMExprTemp rightDestTemp = new ASMExprTemp(newTemp());
                ASMExpr rightDest = node.left().matchLow(
                        // no, this can't be extracted into a variable
                        binOpLogicAddAllAccept(rightDestTemp, instrs),
                        binOpLogicAddAllAccept(rightDestTemp, instrs),
                        binOpLogicAddAllAccept(rightDestTemp, instrs),
                        (IRMem m) -> asmMemTileOf(m, instrs),
                        illegalAccessErrorLambda(),
                        (IRTemp l) -> new ASMExprTemp(l.name())
                );

                // CMP leftDest, rightDest
                instrs.add(new ASMInstr_2Arg(
                        ASMOpCode.CMP, leftDest, rightDest
                ));

                // SETcc al
                instrs.add(new ASMInstr_1Arg(
                        setASMOpCodeOf(node.opType()),
                        new ASMExprReg("al")
                ));

                // MOVZX dest, al
                instrs.add(new ASMInstr_2Arg(
                        ASMOpCode.MOVZX,
                        dest,
                        new ASMExprReg("al")
                ));
                return instrs;
        }
        throw new IllegalAccessError();
    }

    public List<ASMInstr> visit(IRCall node, ASMExprTemp destreg) {
        throw new IllegalAccessError();
    }

    /**
     * Adds asm instructions when the condition of the CJump node is a IRBinOp.
     *
     * @param node node.cond() must be IRBinOp instance.
     * @param instrs instructions to add to.
     */
    private void cjumpBinOpToASMInstr(IRCJump node, List<ASMInstr> instrs) {
        IRBinOp cond = (IRBinOp) node.cond();

        // Visit left child and add the relevant moving ASMs.
        ASMExprTemp leftDestTemp = new ASMExprTemp(newTemp());
        ASMExpr leftDest = cond.left().matchLow(
                // no, this can't be extracted into a variable
                binOpLogicAddAllAccept(leftDestTemp, instrs),
                binOpLogicAddAllAccept(leftDestTemp, instrs),
                binOpLogicAddAllAccept(leftDestTemp, instrs),
                (IRMem m) -> asmMemTileOf(m, instrs),
                illegalAccessErrorLambda(),
                (IRTemp l) -> new ASMExprTemp(l.name())
        );

        ASMExprTemp rightDestTemp = new ASMExprTemp(newTemp());
        ASMExpr rightDest = cond.left().matchLow(
                // no, this can't be extracted into a variable
                binOpLogicAddAllAccept(rightDestTemp, instrs),
                binOpLogicAddAllAccept(rightDestTemp, instrs),
                binOpLogicAddAllAccept(rightDestTemp, instrs),
                (IRMem m) -> asmMemTileOf(m, instrs),
                illegalAccessErrorLambda(),
                (IRTemp l) -> new ASMExprTemp(l.name())
        );

        switch (cond.opType()) {
            case EQ:
            case NEQ:
            case LT:
            case GT:
            case LEQ:
            case GEQ:
                // CMP leftDest, rightDest
                instrs.add(new ASMInstr_2Arg(
                        ASMOpCode.CMP, leftDest, rightDest
                ));
                break;
            default:
                // OP leftDest, rightDest
                instrs.add(new ASMInstr_2Arg(
                        // OP dest, r (r is a constant)
                        asmOpCodeOf(cond.opType()),
                        leftDest,
                        rightDest
                ));

                // TEST leftDest, leftDest
                instrs.add(new ASMInstr_2Arg(
                        ASMOpCode.TEST, leftDest, leftDest
                ));
        }

        // Jcc trueLabel
        instrs.add(new ASMInstr_1Arg(
                jmpASMOpCodeOf(cond.opType()),
                new ASMExprName(node.trueLabel())
        ));
    }

    /**
     * Adds the following instructions to the list is:
     * ```
     *  TEST t, t
     *  JNE l
     * ```
     *
     * @param t temp to test.
     * @param l true label to jump to if test results in non-zero.
     * @param is instruction list to add to.
     */
    private void testJumpNEtoLabel(ASMExprTemp t, String l, List<ASMInstr> is) {
        // TEST t, t
        is.add(new ASMInstr_2Arg(ASMOpCode.TEST, t, t));

        // JNE trueLabel (jump to true label if `t & t` != 0)
        is.add(new ASMInstr_1Arg( ASMOpCode.JNE, new ASMExprName(l)));
    }

    public List<ASMInstr> visit(IRCJump node) {
        List<ASMInstr> instrs = new ArrayList<>();
        node.cond().matchLow(
                (IRBinOp c) -> {
                    cjumpBinOpToASMInstr(node, instrs);
                    return null;
                },
                (IRCall c) -> {
                    ASMExprTemp t = new ASMExprTemp(newTemp());
                    instrs.addAll(c.accept(this, t));
                    testJumpNEtoLabel(t, node.trueLabel(), instrs);
                    return null;
                },
                (IRConst c) -> {
                    // c != 0, jump unconditionally to the true label.
                    if (c.value() != 0L) {
                        instrs.add(new ASMInstr_1Arg(
                                ASMOpCode.JMP,
                                new ASMExprName(node.trueLabel())
                        ));
                    }
                    // c is 0, fallthrough (don't add anything to instrs)
                    return null;
                },
                (IRMem c) -> {
                    ASMExprTemp t = new ASMExprTemp(newTemp());
                    instrs.addAll(c.accept(this, t));
                    testJumpNEtoLabel(t, node.trueLabel(), instrs);
                    return null;
                },
                illegalAccessErrorLambda(),
                (IRTemp c) -> {
                    ASMExprTemp t = new ASMExprTemp(c.name());
                    testJumpNEtoLabel(t, node.trueLabel(), instrs);
                    return null;
                }
        );
        return instrs;
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

    private int getNumParams(IRFuncDecl node) {
        String n = node.name();
        String s = n.substring(n.lastIndexOf('_'));
        if (s.startsWith("t")) {
            int numrets = Integer.parseInt(s.substring(1, 2));
            return s.length() - numrets - 1;
        }
        else return s.length() - 1;
    }

    public List<ASMInstr> visit(IRFuncDecl node) {
        List<ASMInstr> instrs = new ArrayList<>();
        int numparams = getNumParams(node);

        //Prologue
        instrs.add(new ASMInstr_1Arg(ASMOpCode.PUSH, new ASMExprReg("rbp")));
        instrs.add(new ASMInstr_2Arg(ASMOpCode.MOV, new ASMExprReg("rbp"),
                new ASMExprReg("rsp")));
        //If rbx,rbp, r12, r13, r14, r15 used, restore before returning
        instrs.add(new ASMInstr_1Arg(ASMOpCode.PUSH, new ASMExprReg("rbx")));
        instrs.add(new ASMInstr_1Arg(ASMOpCode.PUSH, new ASMExprReg("r12")));
        instrs.add(new ASMInstr_1Arg(ASMOpCode.PUSH, new ASMExprReg("r13")));
        instrs.add(new ASMInstr_1Arg(ASMOpCode.PUSH, new ASMExprReg("r14")));
        instrs.add(new ASMInstr_1Arg(ASMOpCode.PUSH, new ASMExprReg("r15")));

        //Body
        HashMap<String, ASMExpr> argvars = new HashMap<>();
        IRStmt body = node.body();
        IRSeq stmts;
        if (body instanceof IRSeq) stmts = (IRSeq) body;
        else stmts = new IRSeq(body);
            for (IRStmt s : stmts.stmts()) {

                if (s instanceof IRMove) {
                    IRMove mov = (IRMove) s;
                    if (mov.target() instanceof IRTemp && mov.source() instanceof IRTemp) {
                            String destname = ((IRTemp) mov.target()).name();
                            String srcname = ((IRTemp) mov.source()).name();
                            if (destname.startsWith("_ARG")) {
                                //Args passed in rdi,rsi,rdx,rcx,r8,r9, (stack in reverse order)
                                int argnum = Integer.parseInt(destname.replaceAll("\\D+", ""));
                                if (argnum == 0) argvars.put(srcname, new ASMExprReg("rdi"));
                                else if (argnum == 1) argvars.put(srcname, new ASMExprReg("rsi"));
                                else if (argnum == 2) argvars.put(srcname, new ASMExprReg("rdx"));
                                else if (argnum == 3) argvars.put(srcname, new ASMExprReg("rcx"));
                                else if (argnum == 4) argvars.put(srcname, new ASMExprReg("r8"));
                                else if (argnum == 5) argvars.put(srcname, new ASMExprReg("r9"));
                                else {
                                    int stackloc = (numparams - argnum - 5)*8;
                                    argvars.put(srcname, new ASMExprMem(new ASMExprBinOpAdd(new ASMExprReg("rbp"), new ASMExprConst(stackloc))));

                                }
                            }
                            else {
                                if (argvars.containsKey(destname) && argvars.containsKey(srcname))
                                    instrs.add(new ASMInstr_2Arg(
                                            ASMOpCode.MOV,
                                            argvars.get(destname),
                                            argvars.get(srcname)
                                    ));
                                else if (argvars.containsKey(destname)) {
                                    ASMExprTemp tmp = new ASMExprTemp(newTemp());
                                    List<ASMInstr> visited = visitExpr(mov.source(), tmp);
                                    instrs.addAll(visited);
                                    instrs.add(new ASMInstr_2Arg(
                                            ASMOpCode.MOV,
                                            argvars.get(destname),
                                            tmp
                                    ));
                                }
                                else if (argvars.containsKey(srcname)) {
                                    ASMExprTemp tmp = new ASMExprTemp(newTemp());
                                    List<ASMInstr> visited = visitExpr(mov.target(), tmp);
                                    instrs.addAll(visited);
                                    instrs.add(new ASMInstr_2Arg(
                                            ASMOpCode.MOV,
                                            tmp,
                                            argvars.get(srcname)
                                    ));
                                }
                                else {
                                    instrs.addAll(visit(mov));
                                }

                            }

                        }
                    else if (mov.target() instanceof IRTemp) {
                        String destname = ((IRTemp) mov.target()).name();
                        if (argvars.containsKey(destname)) {
                            ASMExprTemp tmp = new ASMExprTemp(newTemp());
                            List<ASMInstr> visited = visitExpr(mov.source(), tmp);
                            instrs.addAll(visited);
                            instrs.add(new ASMInstr_2Arg(
                                    ASMOpCode.MOV,
                                    argvars.get(destname),
                                    tmp
                            ));
                        }
                        else instrs.addAll(visit(mov));
                    }
                    else if (mov.source() instanceof IRTemp) {
                        String srcname = ((IRTemp) mov.source()).name();
                        if (argvars.containsKey(srcname)) {
                            ASMExprTemp tmp = new ASMExprTemp(newTemp());
                            List<ASMInstr> visited = visitExpr(mov.target(), tmp);
                            instrs.addAll(visited);
                            instrs.add(new ASMInstr_2Arg(
                                    ASMOpCode.MOV,
                                    tmp,
                                    argvars.get(srcname)
                            ));
                        }
                    }
                    else {
                        instrs.addAll(visit(mov));
                    }
                    }

                else {
                   instrs.addAll(visitStmt(s));
                }
                }


        //Epilogue
        instrs.add(new ASMInstr_1Arg(ASMOpCode.POP, new ASMExprReg("r15")));
        instrs.add(new ASMInstr_1Arg(ASMOpCode.POP, new ASMExprReg("r14")));
        instrs.add(new ASMInstr_1Arg(ASMOpCode.POP, new ASMExprReg("r13")));
        instrs.add(new ASMInstr_1Arg(ASMOpCode.POP, new ASMExprReg("r12")));
        instrs.add(new ASMInstr_1Arg(ASMOpCode.POP, new ASMExprReg("rbx")));

        instrs.add(new ASMInstr_2Arg(
                ASMOpCode.MOV, new ASMExprReg("rsp"), new ASMExprReg("rbp")
        ));
        instrs.add(new ASMInstr_1Arg(ASMOpCode.POP, new ASMExprReg("rbp")));
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
        instrs.add(new ASMInstr_2Arg(
                ASMOpCode.MOV,
                dest,
                asmMemTileOf(node, instrs)
        ));
        return instrs;
    }

    public List<ASMInstr> visit(IRMove node) {
        IRExpr dest = node.target();
        IRExpr src = node.source();
        if (dest instanceof IRTemp) {
            throw new IllegalAccessError();
            //RHS cases:
            //call -> fresh temp
            //mem -> tile mem as usual
            //name -> error
            //temp -> MOV if different temp otherwise NOP
            //const -> MOV
            //binop -> switch on op
            //if op is not commutative:
            // if LHS is same temp then use single instr
            //if op is commutative:
            // if LHS or RHS is same temp then use single instr
            //otherwise fresh temp
        }
        else if (dest instanceof IRMem) {
            throw new IllegalAccessError();
            //RHS cases:
            //call -> fresh temp
            //mem -> tile mem as usual if not equivalent mem otherwise NOP
            //name -> error
            //temp -> MOV
            //const -> MOV
            //binop -> switch on op
            //if op is not commutative:
            // if LHS of binop is equivalent mem then use single instr
            //if op is commutative:
            // if LHS or RHS of binop is equivalent mem then use single instr
            //otherwise fresh temp
        }
        else {
            throw new IllegalAccessError("only Mem and Temp allowed as destination");
        }
    }

    public List<ASMInstr> visit(IRReturn node) {
        List<ASMInstr> instrs = new ArrayList<>();
        List<IRExpr> retvals = node.rets();
        int numrets = 0;
        for (IRExpr e : retvals) {
            ASMExprTemp tmp = new ASMExprTemp(newTemp());
            List<ASMInstr> visited = visitExpr(e, tmp);
            instrs.addAll(visited);
            if (numrets == 0) {
                instrs.add(new ASMInstr_2Arg(ASMOpCode.MOV, new ASMExprReg("rax"), tmp));
            }
            else if (numrets == 1) {
                instrs.add(new ASMInstr_2Arg(ASMOpCode.MOV, new ASMExprReg("rdx"), tmp));
            }
            else {
                //TODO: return addr of values?
                instrs.add(new ASMInstr_1Arg(ASMOpCode.PUSH, tmp));
            }
            numrets ++;
        }
        return instrs;
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

    private List<ASMInstr> visitExpr(IRExpr e, ASMExprTemp tmp) {
        if (e instanceof IRBinOp) return visit((IRBinOp) e, tmp);
        if (e instanceof IRCall) return visit((IRCall) e, tmp);
        if (e instanceof IRConst) return visit((IRConst) e, tmp);
        if (e instanceof IRMem) return visit((IRMem) e, tmp);
        if (e instanceof IRTemp) return visit((IRTemp) e, tmp);
        throw new IllegalAccessError();
    }

    private List<ASMInstr> visitStmt(IRStmt s) {
        if (s instanceof IRCJump) return visit((IRCJump) s);
        if (s instanceof IRExp) return visit((IRExp) s);
        if (s instanceof IRJump) return visit((IRJump) s);
        if (s instanceof IRLabel) return visit((IRLabel) s);
        if (s instanceof IRMove) return visit((IRMove) s);
        if (s instanceof IRReturn) return visit((IRReturn) s);
        if (s instanceof IRSeq) return visit((IRSeq) s);
        throw new IllegalAccessError();
    }
}
