package edu.cornell.cs.cs4120.xic.ir.visit;

import asm.*;
import edu.cornell.cs.cs4120.xic.ir.*;
import edu.cornell.cs.cs4120.xic.ir.IRBinOp.OpType;
import polyglot.util.Pair;

import java.util.ArrayList;
import java.util.Collections;
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
    Pair<List<ASMInstr>,ASMExprMem> tileMemMult(IRBinOp exp) {
        IRExpr l = exp.left();
        IRExpr r = exp.right();
        if (l instanceof IRConst && validAddrScale((IRConst) l)) {
            if (r instanceof IRTemp) { //[C * T] => [T * C]
                return new Pair<>(
                        new ArrayList<>(),
                        new ASMExprMem(
                                new ASMExprBinOpMult(
                                        toASM((IRTemp) r),
                                        toASM((IRConst) l)
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
                                        toASM((IRConst) l)
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
                                        toASM((IRTemp) l),
                                        toASM((IRConst) r)
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
                                        toASM((IRConst) r)
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
    List<IRExpr> flattenAdds(IRBinOp b){
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
    Pair<List<ASMInstr>, ASMExprMem> tileMemExpr(IRMem m) {
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
                                offset = toASM((IRConst) curr);//new ASMExprConst(((IRConst) curr).value());
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
                                    index_scale = toASM(((IRTemp) curr));
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
                                base = toASM(((IRTemp) remaining));
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
                            throw new IllegalAccessError();
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
                exprToTileMem(this),
                exprToTileMem(this),
                exprToTileMem(this),
                illegalAccessErrorLambda(),
                (IRTemp exp) -> new Pair<>(
                        new ArrayList<>(),
                        new ASMExprMem(toASM(exp))
                )
        );
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

    /**
     * Returns a lambda which adds the assembly instructions of the IRExpr
     * input to the lambda into instrs list. The IRExpr asm instructions are
     * put in temporary dest.
     *
     * @param dest the temporary to put the results of evaluation in.
     * @param instrs instructions to add to.
     */
    private <T extends IRExpr> Function<T, ASMExpr> irBinOpChildToASM(
            ASMExprTemp dest, List<ASMInstr> instrs) {
        return (T e) -> {
            instrs.addAll(e.accept(this, dest));
            return dest;
        };
    }

    // TODO: docstring
    private <T extends IRExpr> Function<T, Pair<List<ASMInstr>, ASMExprMem>>
                                        exprToTileMem(ASMTranslationVisitor v) {
        return exp -> {
            String t0 = newTemp();
            return new Pair<>(
                    exp.accept(v, new ASMExprTemp(t0)),
                    new ASMExprMem(new ASMExprTemp(t0))
            );
        };
    }

    /**
     * Returns the ASMExpr for left and right children of a binary operation,
     * according to the requirements of x86-64 addressing modes. The
     * intermediate asm instructions, which need to execute before the returned
     * ASMExpr are used, are added to the input list of instructions.
     *
     * If the children are complex expressions in themselves, the
     * intermediate asm instructions have the corresponding temporary as the
     * target variable.
     *
     * Postconditions:
     *  - The left ASMExpr is either a Mem or a Temp. It is a Mem only if the
     *  left child input is a Mem (the converse doesn't hold). If it is a
     *  Temp, then it must be leftDestTemp.
     *  - If both IRExprs evaluate to Mems, then the left ASMExpr is a Temp
     *  and the right ASMExpr is a Mem.
     *  - The right ASMExpr is either a Mem or a Temp or a Const, whichever
     *  requires the fewest number of asm instructions to generate. This
     *  means that if the right child is a Temp, this Temp's value is in the
     *  right ASMExpr to avoid a MOV instruction (similarly for Const).
     *
     * @param left child of the binop.
     * @param right child of the binop.
     * @param leftDestTemp destination temp for the left child.
     * @param rightDestTemp destination temp for the right child.
     * @param instrs instructions to add to.
     */
    Pair<ASMExpr, ASMExpr> asmExprOfBinOp(IRExpr left,
                                          IRExpr right,
                                          ASMExprTemp leftDestTemp,
                                          ASMExprTemp rightDestTemp,
                                          List<ASMInstr> instrs) {

        // For ADD and MUL, switching left and right children might
        // seem to improve performance, but there is no point since
        // one of the children will need to be moved to dest anyway
        // after both are computed. So, lhs and rhs computation can
        // be separated.

        // Get the Expr representation of the left side. Depending on
        // the left child of this binop, the repr can be a temp t, a
        // mem location [...] (if the right child is not a mem) etc.
        ASMExpr leftDest = left.matchLow(
                // no, this can't be extracted into a variable
                irBinOpChildToASM(leftDestTemp, instrs),
                irBinOpChildToASM(leftDestTemp, instrs),
                irBinOpChildToASM(leftDestTemp, instrs),
                (IRMem m) -> {
                    ASMExprMem mTile = asmMemTileOf(m, instrs);
                    if (right instanceof IRMem) {
                        // binary ops can't take two IRMems, so put
                        // the left in the dest and return the dest
                        instrs.add(new ASMInstr_2Arg(
                                ASMOpCode.MOV, leftDestTemp, mTile
                        ));
                        return leftDestTemp;
                    } else {
                        return asmMemTileOf(m, instrs);
                    }},
                illegalAccessErrorLambda(),
                irBinOpChildToASM(leftDestTemp, instrs)
        );
        ASMExpr rightDest = right.matchLow(
                // no, this can't be extracted into a variable
                irBinOpChildToASM(rightDestTemp, instrs),
                irBinOpChildToASM(rightDestTemp, instrs),
                // Const on right child can be written as an imm
                ASMTranslationVisitor::toASM,
                // Mem on right child can be written as [...]
                (IRMem r) -> asmMemTileOf(r, instrs),
                illegalAccessErrorLambda(),
                ASMTranslationVisitor::toASM
        );
        return new Pair<>(leftDest, rightDest);
    }

    /**
     * Convert a IR constant to an ASM expression.
     *
     * @param c The constant.
     * @return The ASM expression.
     */
    private static ASMExprConst toASM(IRConst c) {
        return new ASMExprConst(c.value());
    }

    /**
     * Convert an IR temp to an ASM expression.
     *
     * @param t The temp.
     * @return The ASM expression.
     */
    private static ASMExprTemp toASM(IRTemp t) {
        return new ASMExprTemp(t.name());
    }

    /**
     * Get a lambda that throws an illegal access error, compatible for pattern
     * matching against IR expressions.
     *
     * @param <T> The type of the IR expression.
     * @param <U> The return type of the lambda (can be anything since it never
     *           returns).
     * @return The lambda.
     */
    private <T extends IRExpr, U> Function<T, U> illegalAccessErrorLambda() {
        return (T e) -> {
            throw new IllegalAccessError();
        };
    }

    public List<ASMInstr> visit(IRBinOp node, final ASMExprTemp dest) {
        final List<ASMInstr> instrs = new ArrayList<>();
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
            case XOR: {
                // Get exprs for left and right children
                ASMExprTemp rightDestTemp = new ASMExprTemp(newTemp());
                Pair<ASMExpr, ASMExpr> dests = asmExprOfBinOp(
                        // left destination is dest since binary OP will read
                        // and write to dest in the final asm instr
                        node.left(), node.right(), dest, rightDestTemp, instrs
                );

                // mov dest, left
                instrs.add(new ASMInstr_2Arg(
                        ASMOpCode.MOV,
                        dest,
                        dests.part1()
                ));

                // OP dest, right
                instrs.add(new ASMInstr_2Arg(
                        ASMOpCode.asmOpCodeOf(node.opType()),
                        dest,
                        dests.part2()
                ));
                return instrs;
            }
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
            case GEQ: {
                // Get exprs for left and right children
                ASMExprTemp leftDestTemp = new ASMExprTemp(newTemp());
                ASMExprTemp rightDestTemp = new ASMExprTemp(newTemp());
                Pair<ASMExpr, ASMExpr> dests = asmExprOfBinOp(
                        node.left(), node.right(), leftDestTemp,
                        rightDestTemp, instrs
                );

                // CMP leftDest, rightDest
                instrs.add(new ASMInstr_2Arg(
                        ASMOpCode.CMP, dests.part1(), dests.part2()
                ));

                // SETcc al (8-bit version of rax, SETcc supports only 8-bit reg
                instrs.add(new ASMInstr_1Arg(
                        ASMOpCode.setASMOpCodeOf(node.opType()),
                        new ASMExprReg("al")
                ));

                // MOVZX dest, al (zero extend and move 8-bit to 64-bit reg)
                instrs.add(new ASMInstr_2Arg(
                        ASMOpCode.MOVZX,
                        dest,
                        new ASMExprReg("al")
                ));
                return instrs;
            }
        }
        throw new IllegalAccessError();
    }

    public List<ASMInstr> visit(IRCall node, ASMExprTemp destreg) {
        List<ASMInstr> instrs = new ArrayList<>();
        int numargs = node.args().size();
        List<IRExpr> args;
        List<ASMExprReg> argRegs = new ArrayList<>();
        argRegs.add(new ASMExprReg("rdi"));
        argRegs.add(new ASMExprReg("rsi"));
        argRegs.add(new ASMExprReg("rdx"));
        argRegs.add(new ASMExprReg("rcx"));
        argRegs.add(new ASMExprReg("r8"));
        argRegs.add(new ASMExprReg("r9"));
        //Args passed in rdi,rsi,rdx,rcx,r8,r9
        //Rest are passed on (stack in reverse order)
        if (numargs > 6) {
            List<IRExpr> extra_args = node.args().subList(6, numargs-1);
            args = node.args().subList(0,6);
            Collections.reverse(extra_args);
            for (IRExpr e : extra_args) {
                ASMExprTemp tmp = new ASMExprTemp(newTemp());
                List<ASMInstr> visited = visitExpr(e, tmp);
                instrs.addAll(visited);
                instrs.add(new ASMInstr_1Arg(ASMOpCode.PUSH, tmp));
            }
        }
        else args = node.args();
        for (int i = 0; i < args.size(); i++) {
            ASMExprTemp tmp = new ASMExprTemp(newTemp());
            List<ASMInstr> visited = visitExpr(args.get(i), tmp);
            instrs.addAll(visited);
            instrs.add(new ASMInstr_2Arg(ASMOpCode.MOV, argRegs.get(i), tmp));
        }
        ASMExprTemp tmp = new ASMExprTemp(newTemp());
        List<ASMInstr> visited = visitExpr(node.target(), tmp);
        instrs.addAll(visited);
        instrs.add(new ASMInstr_1Arg(ASMOpCode.CALL, tmp));
        instrs.add(new ASMInstr_2Arg(ASMOpCode.MOV, destreg,
                new ASMExprReg("rax")));
        if (numargs > 6) {
            instrs.add(new ASMInstr_2Arg(ASMOpCode.ADD, new ASMExprReg("rsp"),
                    new ASMExprConst(8*(numargs-6))));
        }
        return instrs;
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
        ASMExprTemp rightDestTemp = new ASMExprTemp(newTemp());
        Pair<ASMExpr, ASMExpr> dests = asmExprOfBinOp(
                cond.left(), cond.right(), leftDestTemp, rightDestTemp, instrs
        );
        ASMExpr leftDest = dests.part1(), rightDest = dests.part2();

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

                // Jcc trueLabel
                instrs.add(new ASMInstr_1Arg(
                        ASMOpCode.jmpASMOpCodeOf(cond.opType()),
                        new ASMExprName(node.trueLabel())
                ));
                break;
            default:
                // OP leftDest, rightDest
                instrs.add(new ASMInstr_2Arg(
                        // OP dest, r (r is a constant)
                        ASMOpCode.asmOpCodeOf(cond.opType()),
                        leftDest,
                        rightDest
                ));

                // TEST leftDest, leftDest
                if (leftDest instanceof ASMExprTemp) {
                    testJumpNEtoLabel(
                            (ASMExprTemp) leftDest, node.trueLabel(), instrs
                    );
                } else {
                    // since both arguments to TEST are leftDest, leftDest
                    // must be a temp. So move left to a temp, and TEST on
                    // the temp
                    ASMExprTemp t = new ASMExprTemp(newTemp());
                    instrs.add(new ASMInstr_2Arg(ASMOpCode.MOV, t, leftDest));
                    testJumpNEtoLabel(t, node.trueLabel(), instrs);
                }
        }

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
                    ASMExprTemp t = toASM(c);
                    testJumpNEtoLabel(t, node.trueLabel(), instrs);
                    return null;
                }
        );
        return instrs;
    }

    public List<ASMInstr> visit(IRCompUnit node) {
        List<ASMInstr> instrs = new ArrayList<>();
        for (IRFuncDecl f : node.functions().values()) {
            instrs.addAll(visit(f));
        }
        return instrs;
    }

    public List<ASMInstr> visit(IRConst node, ASMExprTemp dest) {
        //c => MOV dest c
        List<ASMInstr> instrs = new ArrayList<>();
        instrs.add(new ASMInstr_2Arg(
                ASMOpCode.MOV,
                dest,
                toASM(node)
        ));
        return instrs;
    }

    public List<ASMInstr> visit(IRExp node) {
        return visitExpr(node.expr(), new ASMExprTemp(newTemp()));
    }

    /**
     * Return the number of parameters that the function declaration takes in.
     * @param node IRFuncDecl instance
     * @return number of parameters
     */
    private int getNumParams(IRFuncDecl node) {
        String n = node.name();
        String s = n.substring(n.lastIndexOf('_'));
        if (s.startsWith("t")) {
            int numrets = Integer.parseInt(s.substring(1, 2));
            return s.length() - numrets - 1;
        }
        else return s.length() - 1;
    }

    /**
     * Return the number of values that the function declaration returns.
     * @param node IRFuncDecl instance
     * @return number of return values
     */
    private int getNumReturns(IRFuncDecl node) {
        String n = node.name();
        String s = n.substring(n.lastIndexOf('_'));
        if (s.startsWith("p")) return 0;
        else if (s.startsWith("t")) {
            return Integer.parseInt(s.replaceAll("\\D+", ""));
        }
        else return 1;
    }

    /**
     * Return the number of local variables that the function uses, erring on
     * the side of too many when necessary.
     * @param node IRFuncDecl instance
     * @return number of local variables
     */
    private int getNumTemps(IRFuncDecl node) {
        //TODO: return actual number of temps instead of max
        if (!(node.body() instanceof IRSeq)) return 2;
        return ((IRSeq) node.body()).stmts().size() * 2;
    }

    //Current memory location in which to store extra return values
    private ASMExprTemp return_value_loc;
    private int return_value_loc_offset;

    public List<ASMInstr> visit(IRFuncDecl node) {
        List<ASMInstr> instrs = new ArrayList<>();
        int numparams = getNumParams(node);
        return_value_loc_offset = 0;

        //Prologue
        instrs.add(new ASMInstr_1Arg(ASMOpCode.PUSH, new ASMExprReg("rbp")));
        instrs.add(new ASMInstr_2Arg(ASMOpCode.MOV, new ASMExprReg("rbp"),
                new ASMExprReg("rsp")));
        instrs.add(new ASMInstr_2Arg(ASMOpCode.SUB, new ASMExprReg("rbp"),
                new ASMExprConst(getNumTemps(node))));
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
                    if (mov.target() instanceof IRTemp &&
                            mov.source() instanceof IRTemp) {

                            String destname = ((IRTemp) mov.target()).name();
                            String srcname = ((IRTemp) mov.source()).name();
                            if (destname.startsWith("_ARG")) {
                                int argnum;
                                //If function has more than 2 returns
                                // first arg is storage location
                                if (getNumReturns(node) > 2) {
                                    argnum = Integer.parseInt(
                                            destname.replaceAll("\\D+",
                                                    "")) -1;
                                    if (argnum == -1)
                                        return_value_loc = new ASMExprTemp(srcname);
                                }
                                //Args passed in rdi,rsi,rdx,rcx,r8,r9
                                //Rest are passed on (stack in reverse order)
                                else argnum = Integer.parseInt(
                                        destname.replaceAll("\\D+",
                                                ""));
                                if (argnum == 0)
                                    argvars.put(srcname, new ASMExprReg("rdi"));
                                else if (argnum == 1)
                                    argvars.put(srcname, new ASMExprReg("rsi"));
                                else if (argnum == 2)
                                    argvars.put(srcname, new ASMExprReg("rdx"));
                                else if (argnum == 3)
                                    argvars.put(srcname, new ASMExprReg("rcx"));
                                else if (argnum == 4)
                                    argvars.put(srcname, new ASMExprReg("r8"));
                                else if (argnum == 5)
                                    argvars.put(srcname, new ASMExprReg("r9"));
                                else {
                                    int stackloc = (numparams - argnum - 5)*8;
                                    argvars.put(srcname,
                                            new ASMExprMem(new ASMExprBinOpAdd(
                                                    new ASMExprReg("rbp"),
                                                    new ASMExprConst(stackloc))));

                                }
                            }
                            else {
                                if (argvars.containsKey(destname)
                                        && argvars.containsKey(srcname))
                                    instrs.add(new ASMInstr_2Arg(
                                            ASMOpCode.MOV,
                                            argvars.get(destname),
                                            argvars.get(srcname)
                                    ));
                                else if (argvars.containsKey(destname)) {
                                    ASMExprTemp tmp = new ASMExprTemp(newTemp());
                                    List<ASMInstr> visited =
                                            visitExpr(mov.source(), tmp);
                                    instrs.addAll(visited);
                                    instrs.add(new ASMInstr_2Arg(
                                            ASMOpCode.MOV,
                                            argvars.get(destname),
                                            tmp
                                    ));
                                }
                                else if (argvars.containsKey(srcname)) {
                                    ASMExprTemp tmp = new ASMExprTemp(newTemp());
                                    List<ASMInstr> visited =
                                            visitExpr(mov.target(), tmp);
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
                instrs.add(new ASMInstr_2Arg(ASMOpCode.MOV,
                        new ASMExprReg("rax"), tmp));
            }
            else if (numrets == 1) {
                instrs.add(new ASMInstr_2Arg(ASMOpCode.MOV,
                        new ASMExprReg("rdx"), tmp));
            }
            else {
                instrs.add(new ASMInstr_2Arg(ASMOpCode.MOV,
                        new ASMExprBinOpAdd(new ASMExprMem(return_value_loc),
                        new ASMExprConst(return_value_loc_offset)), tmp));
                return_value_loc_offset += 8;
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
                toASM(node)
        ));
        return instrs;
    }

    private List<ASMInstr> visitExpr(IRExpr e, ASMExprTemp tmp) {
        return e.matchLow(
                (IRBinOp n) -> visit(n, tmp),
                (IRCall n) -> visit(n, tmp),
                (IRConst n) -> visit(n, tmp),
                (IRMem n) -> visit(n, tmp),
                illegalAccessErrorLambda(),
                (IRTemp n) -> visit(n, tmp)
        );
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
