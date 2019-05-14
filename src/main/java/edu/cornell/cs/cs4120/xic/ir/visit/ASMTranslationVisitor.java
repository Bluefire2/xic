package edu.cornell.cs.cs4120.xic.ir.visit;

import edu.cornell.cs.cs4120.xic.ir.*;
import edu.cornell.cs.cs4120.xic.ir.IRBinOp.OpType;
import kc875.asm.*;
import kc875.utils.XiUtils;
import polyglot.util.InternalCompilerError;
import polyglot.util.Pair;

import java.util.*;
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
     * Returns true if Const i is either 1, 2, 4, or 8. That is, if i is a
     * valid address scale.
     *
     * @param i constant.
     */
    private static boolean validAddrScale(IRConst i) {
        return i.value() == 1
                || i.value() == 2
                || i.value() == 4
                || i.value() == 8;
    }

    /**
     * Returns true if the value in i is actually an int (<= 32 bits). False
     * if 64 bits are needed to represent i.
     *
     * @param i constant to check.
     */
    private static boolean isInt(IRConst i) {
        return i.value() == (int) i.value();
    }

    /**
     * Helper function for tiling inside of nodes that are MEM(a * b) format
     * Returns assembly instructions required to tile the children
     * as well as the memory address expression for the root of the MEM tree
     *
     * @param b binop representing inside of MEM node - must be MUL operation
     */
    Pair<List<ASMInstr>, ASMExprMem> tileMemMult(IRBinOp b) {
        IRExpr l = b.left();
        IRExpr r = b.right();
        if (l instanceof IRConst && validAddrScale((IRConst) l)) {
            if (r instanceof IRTemp) { //[C * T] => [T * C]
                List<ASMInstr> instrs = new ArrayList<>();
                return new Pair<>(instrs, new ASMExprMem(
                        new ASMExprBinOpMult(
                                toASM((IRTemp) r), toASM((IRConst) l, instrs)
                        )
                ));
            } else {//[C * non-temp] => [t * C]
                ASMExprTemp t = new ASMExprTemp(newTemp());
                List<ASMInstr> instrs = new ArrayList<>(r.accept(this, t));
                return new Pair<>(instrs, new ASMExprMem(
                        new ASMExprBinOpMult(
                                t, toASM((IRConst) l, instrs)
                        )
                ));
            }
        } else if (r instanceof IRConst && validAddrScale((IRConst) r)) {
            if (l instanceof IRTemp) { //[T * C] (don't switch)
                List<ASMInstr> instrs = new ArrayList<>();
                return new Pair<>(instrs, new ASMExprMem(
                        new ASMExprBinOpMult(
                                toASM((IRTemp) l), toASM((IRConst) r, instrs)
                        )
                ));
            } else {//[non-temp * C] => [t * C]
                ASMExprTemp t = new ASMExprTemp(newTemp());
                List<ASMInstr> instrs = new ArrayList<>(l.accept(this, t));
                return new Pair<>(instrs, new ASMExprMem(
                        new ASMExprBinOpMult(
                                t, toASM((IRConst) r, instrs)
                        )
                ));
            }
        } else {
            //[non-const * non-const] => tile binop and put it in a temp
            //result will be stored in index reg and treated as if no scale
            ASMExprTemp t = new ASMExprTemp(newTemp());
            return new Pair<>(b.accept(this, t), new ASMExprMem(t));
        }
    }

    /**
     * Returns a list of IR Expressions that result from flattening the
     * add expressions at the root of the input IR tree. If the root
     *
     * example: (+ (+ a b) (+ c (* d e))) => (a,b,c,(* d e))
     *
     * @param b the input binop if not an add then the returned list will just contain itself
     */
    List<IRExpr> flattenAdds(IRBinOp b) {
        List<IRExpr> exps = new ArrayList<>();
        if (b.opType() == OpType.ADD) {
            if (b.left() instanceof IRBinOp
                    && ((IRBinOp) b.left()).opType() == OpType.ADD) {
                exps.addAll(flattenAdds((IRBinOp) b.left()));
            } else {
                exps.add(b.left());
            }
            if (b.right() instanceof IRBinOp
                    && ((IRBinOp) b.right()).opType() == OpType.ADD) {
                exps.addAll(flattenAdds((IRBinOp) b.right()));
            } else {
                exps.add(b.right());
            }
        } else {
            exps.add(b);
        }
        return exps;
    }

    /**
     * For tiling MEM nodes as assembly memory addresses as opposed to instructions
     * Format must match the following pattern:
     * [base + index * scale + offset]
     * where base and index are temps, scale is 1/2/4/8, and offset is 32 bits
     * any (but not all) of the parts may be missing
     *
     * Table from https://cs.nyu.edu/courses/fall10/V22.0201-002/addressing_modes.pdf
     * +-------------+----------------------------+
     * | Mode        | Intel                      |
     * +-------------+----------------------------+
     * | Absolute    | MOV EAX, [0100]            |
     * | Register    | MOV EAX, [ESI]             |
     * | Reg + Off   | MOV EAX, [EBP-8]           |
     * | R*W + Off   | MOV EAX, [EBX*4 + 0100]    |
     * | B + R*W + O | MOV EAX, [EDX + EBX*4 + 8] |
     * +-------------+----------------------------+
     * effectively, missing parts default to 0 except for scale which defaults to 1
     *
     * Returns assembly instructions required to tile the children
     * as well as the memory address expression for the root of the MEM tree
     *
     * @param m input MEM node
     */
    Pair<List<ASMInstr>, ASMExprMem> tileMemExpr(IRMem m) {
        return m.expr().matchLow(
                (IRBinOp e) -> {
                    if (e.opType() == OpType.ADD) {//[a + b]
                        List<ASMInstr> instrs = new ArrayList<>();
                        //flatten all the + ops into a list
                        List<IRExpr> flattened = flattenAdds(e);
                        //find B, I, S, and O from the flattened exprs
                        ASMExprTemp base = null;
                        ASMExpr index_scale = null;
                        ASMExprConst offset = null;
                        //O will be a const that is 32 bits
                        //try to find O, and remove if found
                        for (int i = 0; i < flattened.size(); i++) {
                            IRExpr curr = flattened.get(i);
                            if (curr instanceof IRConst
                                    && isInt((IRConst) curr)) {
                                // toASM will return an ExprConst after this
                                // isInt check
                                offset = (ASMExprConst) toASM(
                                        (IRConst) curr, instrs
                                );
                                flattened.remove(i);
                                break;
                            }
                        }
                        //first try and find I * S, remove if found
                        //currently matches all MUL regardless if they match the format
                        //will end up with either an ASM mult or an ASM temp
                        for (int i = 0; i < flattened.size(); i++) {
                            IRExpr curr = flattened.get(i);
                            if (curr instanceof IRBinOp
                                    && ((IRBinOp) curr).opType() == OpType.MUL) {
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

                        //size check because objects that match with
                        //offset, index, scale were removed
                        if (flattened.size() != 0){
                            IRExpr remaining = flattened.stream()
                                    .reduce((a, b) -> new IRBinOp(
                                            OpType.ADD, a, b)
                                    ).get();
                            if (remaining instanceof IRTemp) {//only one temp left
                                base = toASM(((IRTemp) remaining));
                            } else { //bunch of temps left, need to calculate
                                ASMExprTemp t = new ASMExprTemp(newTemp());
                                instrs.addAll(remaining.accept(this, t));
                                base = t;
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
                    } else if (e.opType() == OpType.MUL) {
                        //[a * b]
                        //use helper function
                        return tileMemMult(e);
                    } else {
                        //[a op b] where op is not + or *
                        //calculate using instructions and put into a temp
                        ASMExprTemp t = new ASMExprTemp(newTemp());
                        return new Pair<>(
                                e.accept(this, t), new ASMExprMem(t)
                        );
                    }
                },
                exprToTileMem(this),
                exprToTileMem(this),
                exprToTileMem(this),
                illegalAccessErrorLambda(),
                (IRTemp e) -> new Pair<>(
                        new ArrayList<>(), new ASMExprMem(toASM(e))
                ),
                (IRExprLabel e) -> new Pair<>(
                        new ArrayList<>(), new ASMExprMem(toASM(e))
                ),
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
            ASMExprRT dest, List<ASMInstr> instrs) {
        return (T e) -> {
            instrs.addAll(e.accept(this, dest));
            return dest;
        };
    }

    /**
     * Returns a lambda which tiles the IRExpr input as a memory access
     * The lambda takes in the inside of an IRMem (some IRExpr e) and returns a pair:
     * instructions storing the result of e in a temp t, and [t]
     *
     * @param v the visitor that is used to translate the lambda input to assembly
     */
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
     *  right ASMExpr to avoid a MOV instruction (similarly for Const if less
     *  than 32 bits; if not then the Const is moved to a temp and that temp
     *  is returned).
     *
     * @param left child of the binop.
     * @param right child of the binop.
     * @param leftDestTemp destination temp for the left child.
     * @param rightDestTemp destination temp for the right child.
     * @param instrs instructions to add to.
     */
    Pair<ASMExpr, ASMExpr> asmExprOfBinOp(IRExpr left,
                                          IRExpr right,
                                          ASMExprRT leftDestTemp,
                                          ASMExprRT rightDestTemp,
                                          List<ASMInstr> instrs) {
        if (leftDestTemp == null && !(left instanceof IRTemp || left instanceof IRMem)){
            throw new IllegalAccessError(
                    "if no destination provided, LHS must be a temp or mem"
            );
        }
        if (left instanceof IRTemp && leftDestTemp == null) {
            leftDestTemp = new ASMExprTemp(((IRTemp) left).name());
        }
        // For ADD and MUL, switching left and right children might
        // seem to improve performance, but there is no point since
        // one of the children will need to be moved to dest anyway
        // after both are computed. So, lhs and rhs computation can
        // be separated.

        ASMExpr rightDest = right.matchLow(
                // no, this can't be extracted into a variable
                irBinOpChildToASM(rightDestTemp, instrs),
                irBinOpChildToASM(rightDestTemp, instrs),
                // Const on right child can be written as an imm
                (IRConst c) -> toASM(c, instrs),
                // Mem on right child can be written as [...]
                (IRMem m) -> {
                    ASMExprMem mTile = asmMemTileOf(m, instrs);
                    if (left instanceof IRMem) {
                        // binary ops can't take two IRMems, so put
                        // the left in the dest and return the dest
                        instrs.add(new ASMInstr_2Arg(
                                ASMOpCode.MOV, rightDestTemp, mTile
                        ));
                        return rightDestTemp;
                    } else {
                        return asmMemTileOf(m, instrs);
                    }},
                illegalAccessErrorLambda(),
                this::toASM
        );
        // Get the Expr representation of the left side. Depending on
        // the left child of this binop, the repr can be a temp t, a
        // mem location [...] (if the right child is not a mem) etc.
        ASMExpr leftDest = left.matchLow(
                // no, this can't be extracted into a variable
                irBinOpChildToASM(leftDestTemp, instrs),
                irBinOpChildToASM(leftDestTemp, instrs),
                irBinOpChildToASM(leftDestTemp, instrs),
                (IRMem m) -> asmMemTileOf(m, instrs),
                illegalAccessErrorLambda(),
                irBinOpChildToASM(leftDestTemp, instrs)
        );
        return new Pair<>(leftDest, rightDest);
    }

    /**
     * Convert a IR constant to an ASM expression. If the constant is 32 bits
     * or less, an ASMExprConst is returned. Otherwise, the constant is moved
     * to a 64-bit temporary and the temporary is returned.
     *
     * @param c The constant.
     * @param instrs Instructions to add to.
     * @return The ASM expression.
     */
    private ASMExpr toASM(IRConst c, List<ASMInstr> instrs) {
        long val = c.value();
        if (isInt(c))
            // const is actually less than 32 bits
            return new ASMExprConst(val);
        else {
            // const is more than 32 bits. Move it to a temp and then return
            // the temp
            ASMExprTemp t = new ASMExprTemp(newTemp());
            instrs.add(new ASMInstr_2Arg(
                    ASMOpCode.MOV, t, new ASMExprConst(val)
            ));
            return t;
        }
    }

    /**
     * Convert an IR temp to an ASM expression.
     *
     * @param t The temp.
     * @return The ASM expression.
     */
    private ASMExprTemp toASM(IRTemp t) {
        return new ASMExprTemp(t.name());
    }


    private ASMExprLabel toASM(IRExprLabel l) {
        return new ASMExprLabel(l.getName());
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

    //special behavior - if dest is null, result is stored in LHS operand, if it is mem or temp
    public List<ASMInstr> visit(IRBinOp node, ASMExprRT dest) {
        final List<ASMInstr> instrs = new ArrayList<>();
        if (dest == null && !(node.left() instanceof IRTemp || node.left() instanceof IRMem)){
            throw new IllegalAccessError(
                    "if no destination provided, LHS must be a temp or mem"
            );
        }
        //set LHS to dest if no dest is provided and LHS is a temp
        if (dest == null && node.left() instanceof IRTemp){
            dest = new ASMExprTemp(((IRTemp) node.left()).name());
        }
        switch (node.opType()) {
            case LSHIFT:
            case RSHIFT:
            case ARSHIFT:
                throw new InternalCompilerError(
                        node.opType() + " not supported"
                );
            case ADD:
            case SUB:
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
                        node.left(), node.right(),
                        dest, rightDestTemp, instrs
                );

                if (dests.part1() instanceof ASMExprMem && dest != null) {
                    // since this is a binop with a target dest, we need to
                    // move the left part to dest if the left part is a
                    // memory expression
                    instrs.add(new ASMInstr_2Arg(
                            ASMOpCode.MOV,
                            dest,
                            dests.part1()
                    ));
                }
                // else part1 is dest, using asmExprOfBinOp's postconditions
                // OP left, right
                instrs.add(new ASMInstr_2Arg(
                        ASMOpCode.asmOpCodeOf(node.opType()),
                        dests.part1(),
                        dests.part2()
                ));
                return instrs;
            }
            case MUL:
            case HMUL:
            case DIV:
            case MOD: {
                // idiv x -> rax = rax / x, rdx = rax % x
                // imul x -> rdx:rax = rax * x
                // so we have to do a bit of gymnastics to get this to work

                boolean useRAX = node.opType() == OpType.DIV
                        || node.opType() == OpType.MUL;

                // we want to do left / right
                // rax <- left
                // t <- right
                ASMExprTemp leftDestTemp = new ASMExprTemp(newTemp()); // rax
                ASMExprTemp rightDestTemp = new ASMExprTemp(newTemp()); // t
                Pair<ASMExpr, ASMExpr> dests = asmExprOfBinOp(
                        node.left(), node.right(),
                        leftDestTemp, rightDestTemp, instrs
                );

                //TODO: need 2nd pass to remove back to back push/pops

                instrs.add(new ASMInstr_1Arg(
                        ASMOpCode.PUSH,
                        new ASMExprReg("rax")
                ));
                instrs.add(new ASMInstr_1Arg(
                        ASMOpCode.PUSH,
                        new ASMExprReg("rdx")
                ));



                if (dests.part1() instanceof ASMExprMem) {
                    // left needs to be moved into rax
                    instrs.add(new ASMInstr_2Arg(
                            ASMOpCode.MOV, new ASMExprReg("rax"), dests.part1()
                    ));
                }
                else instrs.add(new ASMInstr_2Arg(ASMOpCode.MOV, new ASMExprReg("rax"), leftDestTemp));

                //move 0 to rdx
                instrs.add(new ASMInstr_2Arg(
                        ASMOpCode.MOV,
                        new ASMExprReg("rdx"),
                        new ASMExprConst(0)
                ));

                ASMExpr rightDest = dests.part2();

                if (dests.part2() instanceof ASMExprConst) {
                    // right needs to be moved into t
                    instrs.add(new ASMInstr_2Arg(
                            ASMOpCode.MOV, rightDestTemp, dests.part2()
                    ));
                    rightDest = rightDestTemp;
                }

                // now we do idiv/imul dests.part2()
                instrs.add(new ASMInstr_1Arg(
                        // this takes care of choosing the opcode
                        ASMOpCode.asmOpCodeOf(node.opType()), rightDest
                ));

                // finally, move the result into dest
                // if we want the quotient or the mul result, we take rax
                // if we want the remainder or the hmul result, we take rdx
                if (dest == null) {
                    instrs.add(new ASMInstr_2Arg(
                            ASMOpCode.MOV, dests.part1(),
                            useRAX ? new ASMExprReg("rax") : new ASMExprReg("rdx")
                    ));
                } else {
                    instrs.add(new ASMInstr_2Arg(
                            ASMOpCode.MOV, dest,
                            useRAX ? new ASMExprReg("rax") : new ASMExprReg("rdx")
                    ));
                }

                instrs.add(new ASMInstr_1Arg(
                        ASMOpCode.POP,
                        new ASMExprReg("rdx")
                ));
                instrs.add(new ASMInstr_1Arg(
                        ASMOpCode.POP,
                        new ASMExprReg("rax")
                ));

                return instrs;
            }
            case EQ:
            case NEQ:
            case LT:
            case GT:
            case LEQ:
            case GEQ: {
                // Get exprs for left and right children
                ASMExprTemp rightDestTemp = new ASMExprTemp(newTemp());
                Pair<ASMExpr, ASMExpr> dests = asmExprOfBinOp(
                        node.left(), node.right(),
                        dest, rightDestTemp, instrs
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
                if (dest == null) {
                    if (dests.part1() instanceof ASMExprTemp) {
                        instrs.add(new ASMInstr_2Arg(
                                ASMOpCode.MOVZX,
                                dests.part1(),
                                new ASMExprReg("al")
                        ));
                    } else {
                        String t = newTemp();
                        instrs.add(new ASMInstr_2Arg(
                                ASMOpCode.MOVZX,
                                new ASMExprTemp(t),
                                new ASMExprReg("al")
                        ));
                        instrs.add(new ASMInstr_2Arg(
                                ASMOpCode.MOV,
                                dests.part1(),
                                new ASMExprTemp(t)
                        ));
                    }
                } else {
                    instrs.add(new ASMInstr_2Arg(
                            ASMOpCode.MOVZX,
                            dest,
                            new ASMExprReg("al")
                    ));
                }
                return instrs;
            }
        }
        throw new IllegalAccessError();
    }

    public List<ASMInstr> visit(IRCall node, ASMExprRT destreg) {
        List<ASMInstr> instrs = new ArrayList<>();

        if (!(node.target() instanceof IRName)) throw new IllegalAccessError();
        String name = ((IRName) node.target()).name();

        List<ASMExprReg> argRegs = Arrays.asList(
                new ASMExprReg("rdi"),
                new ASMExprReg("rsi"),
                new ASMExprReg("rdx"),
                new ASMExprReg("rcx"),
                new ASMExprReg("r8"),
                new ASMExprReg("r9")
        );

        int numArgs = node.args().size();
        int numRets = XiUtils.getNumReturns(name);

        // Evaluate each func arg
        List<ASMExprTemp> argTemps = new ArrayList<>();
        for (IRExpr e : node.args()) {
            ASMExprTemp t = new ASMExprTemp(newTemp());
            List<ASMInstr> visited = visitExpr(e, t);
            instrs.addAll(visited);
            argTemps.add(t);
        }

        instrs.add(new ASMInstrComment("CALL_START"));

        List<ASMExprTemp> argsInRegs;
        List<ASMExprTemp> argsOnStack = new ArrayList<>();
        if (numRets > 2) {
            // Create scratch space for extra return values
            // SUB rsp, 8*(numRets-2)
            instrs.add(new ASMInstr_2Arg(
                    ASMOpCode.SUB,
                    new ASMExprReg("rsp"),
                    new ASMExprConst(8 * (numRets-2))
            ));

            // MOV rdi, rsp
            instrs.add(new ASMInstr_2Arg(
                    ASMOpCode.MOV, new ASMExprReg("rdi"), new ASMExprReg("rsp")
            ));

            // Allocate 6th+ args on the stack
            if (numArgs > 5) {
                argsOnStack = argTemps.subList(5, numArgs);
                argsInRegs = argTemps.subList(0, 5);
            } else {
                // argsOnStack is empty
                argsInRegs = argTemps;
            }
        } else {
            // num of rets <= 2
            // Allocate 7th+ args on the stack
            if (numArgs > 6) {
                argsOnStack = argTemps.subList(6, numArgs);
                argsInRegs = argTemps.subList(0, 6);
            } else {
                // argsOnStack is empty
                argsInRegs = argTemps;
            }
        }

        // Push argsOnStack in reverse order
        Collections.reverse(argsOnStack);
        for (ASMExprTemp t : argsOnStack) {
            instrs.add(new ASMInstr_1Arg(ASMOpCode.PUSH, t));
        }

        // Move each arg into corresponding regs
        for (int i = 0; i < argsInRegs.size(); i++) {
            int regIndex = (numRets > 2) ? i + 1 : i;
            instrs.add(new ASMInstr_2Arg(
                    ASMOpCode.MOV, argRegs.get(regIndex), argsInRegs.get(i)
            ));
        }

        // Function call
        instrs.add(new ASMInstr_1Arg(ASMOpCode.CALL, new ASMExprName(name)));

        // Free space of argsOnStack
        if (argsOnStack.size() > 0) {
            instrs.add(new ASMInstr_2Arg(
                    ASMOpCode.ADD,
                    new ASMExprReg("rsp"),
                    new ASMExprConst(8*argsOnStack.size())
            ));
        }

        // Move the return values to _RETi
        switch (numRets) {
            case 0: break;
            case 1: {
                instrs.add(new ASMInstr_2Arg(
                        ASMOpCode.MOV,
                        new ASMExprTemp("_RET0"),
                        new ASMExprReg("rax")
                ));
            } break;
            case 2: {
                instrs.add(new ASMInstr_2Arg(
                        ASMOpCode.MOV,
                        new ASMExprTemp("_RET0"),
                        new ASMExprReg("rax")
                ));
                instrs.add(new ASMInstr_2Arg(
                        ASMOpCode.MOV,
                        new ASMExprTemp("_RET1"),
                        new ASMExprReg("rdx")
                ));
            } break;
            default: {
                instrs.add(new ASMInstr_2Arg(
                        ASMOpCode.MOV,
                        new ASMExprTemp("_RET0"),
                        new ASMExprReg("rax")
                ));
                instrs.add(new ASMInstr_2Arg(
                        ASMOpCode.MOV,
                        new ASMExprTemp("_RET1"),
                        new ASMExprReg("rdx")
                ));

                // For i >= 2, move from stack to RETi
                for (int i = 2; i < numRets; i++) {
                    // _RET2 is highest on the stack, _RETn is lowest. So
                    // stackLoc is calculated as numrets - i. 1 deducted
                    // because how stack addresses work
                    // MOV _RETi, [rsp + 8*(i-2)]
                    instrs.add(new ASMInstr_2Arg(
                            ASMOpCode.MOV,
                            new ASMExprTemp("_RET" + i),
                            new ASMExprMem(new ASMExprBinOpAdd(
                                    new ASMExprReg("rsp"),
                                    new ASMExprConst(8 * (i-2))
                            ))
                    ));
                }

                // Free up space for return values, ADD rsp, 8*(n-2)
                instrs.add(new ASMInstr_2Arg(
                        ASMOpCode.ADD,
                        new ASMExprReg("rsp"),
                        new ASMExprConst(8 * (numRets-2))
                ));
            }
        }

        instrs.add(new ASMInstrComment("CALL_END"));

        // Move rax into destreg
        instrs.add(new ASMInstr_2Arg(
                ASMOpCode.MOV, destreg, new ASMExprTemp("_RET0")
        ));
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

    public List<ASMInstr> visit(IRConst node, ASMExprRT dest) {
        //c => MOV dest c
        List<ASMInstr> instrs = new ArrayList<>();
        instrs.add(new ASMInstr_2Arg(ASMOpCode.MOV, dest, toASM(node, instrs)));
        return instrs;
    }

    public List<ASMInstr> visit(IRExp node) {
        return visitExpr(node.expr(), new ASMExprTemp(newTemp()));
    }

    public List<ASMInstr> visit(IRFuncDecl node) {
        List<ASMInstr> instrs = new ArrayList<>();
        int numParams = XiUtils.getNumParams(node.name());

        instrs.add(new ASMInstrLabel(node.name()));
        //Prologue
        // https://www.cs.cornell.edu/courses/cs4120/2019sp/lectures/18callconv/lec18-sp18.pdf
        // can use ENTER 0, 0 to denote:
        // PUSH rbp
        // MOV rbp, rsp
        // SUB rsp, 0
        instrs.add(new ASMInstr_2Arg(
                ASMOpCode.ENTER, new ASMExprConst(0), new ASMExprConst(0)
        ));

        //Body
        IRStmt body = node.body();
        IRSeq stmts = body instanceof IRSeq ? (IRSeq) body : new IRSeq(body);

        int numRets = XiUtils.getNumReturns(node.name());
        if (numRets > 2 && numParams == 0) {
            // procedure with multiple returns. The body won't contain any
            // references to _ARG0, which we need here for the return asm at
            // the end. So add it before rest of body
            instrs.add(new ASMInstr_2Arg(
                    ASMOpCode.MOV,
                    new ASMExprTemp("_ARG0"),
                    new ASMExprReg("rdi")
            ));
        }

        int totalParams = numRets > 2 ? numParams + 1 : numParams;
        for (int argNum = 0; argNum < totalParams; argNum++) {
            ASMExpr replace_ARGi;
            switch (argNum) {
                case 0: replace_ARGi = new ASMExprReg("rdi"); break;
                case 1: replace_ARGi = new ASMExprReg("rsi"); break;
                case 2: replace_ARGi = new ASMExprReg("rdx"); break;
                case 3: replace_ARGi = new ASMExprReg("rcx"); break;
                case 4: replace_ARGi = new ASMExprReg("r8"); break;
                case 5: replace_ARGi = new ASMExprReg("r9"); break;
                default:
                    // rbp + 0 is old rbp; rbp + 8 is old rip
                    // so the ith arg is at [rbp + 8*((i-1)-6+2)] or
                    // [rbp + 16] where i >= 7 (i-1) due to 0-indexing
                    replace_ARGi = new ASMExprMem(new ASMExprBinOpAdd(
                            new ASMExprReg("rbp"),
                            new ASMExprConst(8 * (argNum - 6 + 2))
                    ));
            }
            // mov _ARGi, r**
            instrs.add(new ASMInstr_2Arg(
                    ASMOpCode.MOV,
                    new ASMExprTemp("_ARG" + argNum),
                    replace_ARGi
            ));
        }

        // visit the body
        if (numRets > 2) {
            // Visit the body; if numRets > 2, then replace _ARGi with _ARG(i+1).
            // We can use the CopyPropagationVisitor's visit function to this
            CopyPropagationVisitor cpv = new CopyPropagationVisitor();
            Map<IRTemp, IRTemp> argToNewArg = new HashMap<>();
            for (int argNum = 0; argNum < numParams; argNum++) {
                argToNewArg.put(
                        new IRTemp("_ARG" + argNum),
                        new IRTemp("_ARG" + (argNum + 1))
                );
            }
            stmts.stmts().forEach(
                    s -> instrs.addAll(visitStmt(cpv.visit(s, argToNewArg)))
            );
        } else {
            stmts.stmts().forEach(s -> instrs.addAll(visitStmt(s)));
        }

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

    public List<ASMInstr> visit(IRMem node, ASMExprRT dest) {
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

    private void moveHelper(IRExpr src, IRExpr dest, List<ASMInstr> instrs){
        // case 3 (fallback): the source is an expression that is not a mem
        // or a temp or a const
        // move x, e (where x is a temp or a mem)
        // We accept e which generates instructions for it and places the
        // result in a destination temp t. Then, we just do:
        // mov x, t

        // Get the destination ASM representation
        ASMExpr x = asmExprOfMemOrTemp(dest, instrs);
        ASMExprTemp t = new ASMExprTemp(newTemp());
        instrs.addAll(src.accept(this, t));
        instrs.add(new ASMInstr_2Arg(ASMOpCode.MOV, x, t));
    }

    public List<ASMInstr> visit(IRMove node) {
        IRExpr dest = node.target();
        IRExpr src = node.source();
        List<ASMInstr> instrs = new ArrayList<>();

        if (!(dest instanceof IRTemp || dest instanceof IRMem)) {
            throw new IllegalAccessError(
                    "only Mem and Temp allowed as destination for move"
            );
        }

        if (dest instanceof IRMem && src instanceof IRMem) {
            /*
             * case 1: both source and dest are mems
             * move [a], [b]
             *
             * mov t, [b]
             * mov [a] t
             */
            ASMExprTemp t = new ASMExprTemp(newTemp());

            ASMExprMem destASM = asmMemTileOf((IRMem) dest, instrs);
            ASMExprMem srcASM = asmMemTileOf((IRMem) src, instrs);

            instrs.add(new ASMInstr_2Arg(ASMOpCode.MOV, t, srcASM));
            instrs.add(new ASMInstr_2Arg(ASMOpCode.MOV, destASM, t));
        } else if (src instanceof IRMem || src instanceof IRTemp) {
            // case 2: the source is a mem or a temp
            // case 2.1: move [a], t -> mov [a], t
            // case 2.2: move t, [a] -> mov t, [a]

            ASMExpr destASM = asmExprOfMemOrTemp(dest, instrs);
            ASMExpr srcASM = asmExprOfMemOrTemp(src, instrs);

            instrs.add(new ASMInstr_2Arg(ASMOpCode.MOV, destASM, srcASM));
        } else if (src instanceof IRConst) {
            // case 3: source is a const -> mov t/m, c

            ASMExpr destASM = asmExprOfMemOrTemp(dest, instrs);
            ASMExpr srcASM = toASM((IRConst) src, instrs);

            instrs.add(new ASMInstr_2Arg(ASMOpCode.MOV, destASM, srcASM));

        } else if (src instanceof IRBinOp) {
            IRBinOp srcBinOp = (IRBinOp) src;
            IRConst one = new IRConst(1);
            //MOV(A, A op B)
            if (dest.equals(srcBinOp.left())){
                if (srcBinOp.opType() == OpType.ADD && one.equals(srcBinOp.right())){//INC
                    //must call for each case to avoid side effects...
                    ASMExpr destASM = asmExprOfMemOrTemp(dest, instrs);
                    instrs.add(new ASMInstr_1Arg(ASMOpCode.INC, destASM));
                } else if (srcBinOp.opType() == OpType.SUB && one.equals(srcBinOp.right())){//DEC
                    ASMExpr destASM = asmExprOfMemOrTemp(dest, instrs);
                    instrs.add(new ASMInstr_1Arg(ASMOpCode.DEC, destASM));
                } else {
                    instrs.addAll(srcBinOp.accept(this, null));//spooky
                }
            } else if (dest.equals(srcBinOp.right())){//MOV(A, B op A)
                if (srcBinOp.opType() == OpType.ADD && one.equals(srcBinOp.left())) {//INC
                    ASMExpr destASM = asmExprOfMemOrTemp(dest, instrs);
                    instrs.add(new ASMInstr_1Arg(ASMOpCode.INC, destASM));
                } else {
                    //see if Left and Right can be flipped
                    //that way we can have dest be on the left
                    //if it can't then we will use the default tiling case
                    IRMove flipped;
                    switch (srcBinOp.opType()) {
                        //these are commutative
                        case ADD:
                        case AND:
                        case OR:
                        case XOR:
                        case MUL:
                        case HMUL:
                        case EQ:
                        case NEQ:
                            flipped = new IRMove(dest, new IRBinOp(srcBinOp.opType(), srcBinOp.right(), srcBinOp.left()));
                            instrs.addAll(flipped.accept(this));
                            break;
                        //these require changing the opcode
                        case LT:
                            flipped = new IRMove(dest, new IRBinOp(OpType.GEQ, srcBinOp.right(), srcBinOp.left()));
                            instrs.addAll(flipped.accept(this));
                            break;
                        case GT:
                            flipped = new IRMove(dest, new IRBinOp(OpType.LEQ, srcBinOp.right(), srcBinOp.left()));
                            instrs.addAll(flipped.accept(this));
                            break;
                        case LEQ:
                            flipped = new IRMove(dest, new IRBinOp(OpType.GT, srcBinOp.right(), srcBinOp.left()));
                            instrs.addAll(flipped.accept(this));
                            break;
                        case GEQ:
                            flipped = new IRMove(dest, new IRBinOp(OpType.LT, srcBinOp.right(), srcBinOp.left()));
                            instrs.addAll(flipped.accept(this));
                            break;
                        default:
                            moveHelper(src, dest, instrs);
                    }
                }
            } else {//MOV(A, B op C) - no tile
                moveHelper(src, dest, instrs);
            }
        } else {
            moveHelper(src, dest, instrs);
        }
        return instrs;
    }

    /**
     * Create an ASM expression from an IR mem or temp expression.
     *
     * Preconditions:
     * - {@code expr} is an instance of {@link IRMem} or {@link IRTemp}.
     *
     * @param expr The IR expression.
     * @param instrs The set of ASM instructions to add to.
     * @return The ASM expression.
     */
    private ASMExpr asmExprOfMemOrTemp(IRExpr expr, List<ASMInstr> instrs) {
        return expr instanceof IRMem
                ? asmMemTileOf((IRMem) expr, instrs)
                : new ASMExprTemp(((IRTemp) expr).name());
    }

    public List<ASMInstr> visit(IRReturn node) {
        List<ASMInstr> instrs = new ArrayList<>();
        List<IRExpr> retVals = node.rets();
        ASMExprTemp[] tmps = new ASMExprTemp[retVals.size()];

        for (int i = 0; i < retVals.size(); i++) {
            ASMExprTemp tmp = new ASMExprTemp(newTemp());
            List<ASMInstr> visited = visitExpr(retVals.get(i), tmp);
            instrs.addAll(visited);
            tmps[i] = tmp;
        }

        for (int i = 0; i < retVals.size(); i++) {
            // evaluate ei
            ASMExprTemp tmp = tmps[i];
            switch (i) {
                case 0:
                    // First return value, move to rax
                    instrs.add(new ASMInstr_2Arg(
                            ASMOpCode.MOV, new ASMExprReg("rax"), tmp
                    ));
                    break;
                case 1:
                    // Second return value, move to rdx
                    instrs.add(new ASMInstr_2Arg(
                            ASMOpCode.MOV, new ASMExprReg("rdx"), tmp
                    ));
                    break;
                default:
                    // ith return value, move to [_ARG0 + 8*(i-2)]
                    instrs.add(new ASMInstr_2Arg(
                            ASMOpCode.MOV,
                            new ASMExprMem(new ASMExprBinOpAdd(
                                    new ASMExprTemp("_ARG0"),
                                    new ASMExprConst(8 * (i-2))
                            )),
                            tmp
                    ));
            }
        }

        //Epilogue to this function
        instrs.add(new ASMInstr_0Arg(ASMOpCode.LEAVE));
        instrs.add(new ASMInstr_1Arg(
                ASMOpCode.RET,
                new ASMExprConst(retVals.size())
        ));

        return instrs;
    }

    public List<ASMInstr> visit(IRSeq node) {
        List<ASMInstr> allInstrs = new ArrayList<>();
        node.stmts().forEach(s -> allInstrs.addAll(s.accept(this)));
        return allInstrs;
    }

    public List<ASMInstr> visit(IRTemp node, ASMExprRT dest) {
        List<ASMInstr> instrs = new ArrayList<>();
        //if dest is the same as src then we don't move
        if (dest instanceof ASMExprTemp) {
            if (((ASMExprTemp) dest).getName().equals(node.name())) {
                return instrs;
            }
        }
        //r => MOV dest r
        instrs.add(new ASMInstr_2Arg(
                ASMOpCode.MOV,
                dest,
                toASM(node)
        ));
        return instrs;
    }

    // label tiles to: lea dest, [label]
    @Override
    public List<ASMInstr> visit(IRExprLabel node, ASMExprRT destreg) {
        List<ASMInstr> instrs = new ArrayList<>();
        instrs.add(new ASMInstr_2Arg(
                ASMOpCode.LEA,
                destreg,
                new ASMExprMem(new ASMExprLabel(node.getName()))
                )
        );
        return instrs;
    }

    private List<ASMInstr> visitExpr(IRExpr e, ASMExprRT tmp) {
        return e.matchLow(
                (IRBinOp n) -> visit(n, tmp),
                (IRCall n) -> visit(n, tmp),
                (IRConst n) -> visit(n, tmp),
                (IRMem n) -> visit(n, tmp),
                illegalAccessErrorLambda(),
                (IRTemp n) -> visit(n, tmp),
                (IRExprLabel n) -> visit(n, tmp)
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
