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
    public Pair<List<ASMInstr>,ASMExprMem> tileMemMult(IRBinOp exp) {
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
    public List<IRExpr> flattenAdds(IRBinOp b){
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
        return res;
    }

    /**
     * Get a lambda that takes an IR expression and adds all of its generated
     * assembly instructions to a given list.
     *
     * @param dest The destination register for the current tile.
     * @param instrs The list of instructions to add to.
     * @param <T> The type of the IR expression.
     * @return The lambda.
     */
    private <T extends IRExpr> Function<T, Void> binOpArithAddAllAccept(
            final ASMExprTemp dest, final List<ASMInstr> instrs) {
        return (T e) -> {
            instrs.addAll(e.accept(this, dest));
            return null;
        };
    }

    private <T extends IRExpr> Function<T, ASMExpr> binOpLogicAddAllAccept(
            final ASMExprTemp dest, final List<ASMInstr> instrs) {
        return (T e) -> {
            instrs.addAll(e.accept(this, dest));
            return dest;
        };
    }

    private Function<IRMem, ASMExpr> binOpLogicAddAllAcceptMem(
            final List<ASMInstr> instrs) {
        return (IRMem e) -> {
            Pair<List<ASMInstr>,ASMExprMem> memTile = tileMemExpr(e);
            instrs.addAll(memTile.part1());
            return memTile.part2();
        };
    }

    private Function<IRMem, Void> binOpArithAddAllAcceptMem(
            final ASMExprTemp dest, final List<ASMInstr> instrs) {
        return (IRMem e) -> {
            Pair<List<ASMInstr>, ASMExprMem> memTile = tileMemExpr(e);
            instrs.addAll(memTile.part1());
            instrs.add(new ASMInstr_2Arg(
                    ASMOpCode.MOV,
                    dest,
                    memTile.part2()
            ));
            return null;
        };
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
                            Pair<List<ASMInstr>,ASMExprMem> memTile = tileMemExpr(r);
                            instrs.addAll(memTile.part1());
                            instrs.add(new ASMInstr_2Arg(
                                    asmOpCodeOf(node.opType()),
                                    dest,
                                    memTile.part2()
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
                        binOpLogicAddAllAcceptMem(instrs),
                        illegalAccessErrorLambda(),
                        (IRTemp l) -> new ASMExprTemp(l.name())
                );

                ASMExprTemp rightDestTemp = new ASMExprTemp(newTemp());
                ASMExpr rightDest = node.left().matchLow(
                        // no, this can't be extracted into a variable
                        binOpLogicAddAllAccept(rightDestTemp, instrs),
                        binOpLogicAddAllAccept(rightDestTemp, instrs),
                        binOpLogicAddAllAccept(rightDestTemp, instrs),
                        binOpLogicAddAllAcceptMem(instrs),
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

                if (s instanceof IRReturn) {
                    //First return in rax, second in rdx, rest saved to caller specified memory location
                    IRReturn ret = (IRReturn) s;
                    List<IRExpr> retvals = ret.rets();
                    int numrets = 0;
                    for (IRExpr e : retvals) {
                      //  ASMInstr visited = visit(e);
                        if (numrets == 0) {
                            //TODO: visit IRExpr -> ASMExpr
                            // instrs.add(new ASMInstrMove(ASMOpCode.MOV, new ASMExprReg("rax"), visited));
                        }
                        else if (numrets == 1) {
                            //  instrs.add(new ASMInstrMove(ASMOpCode.MOV, new ASMExprReg("rdx"), visited));
                        }
                        else {} //Already handled in function body?
                        numrets ++;
                    }
                    instrs.add(new ASMInstr_0Arg(ASMOpCode.RET));
                }

                else if (s instanceof IRMove) {
                    IRMove mov = (IRMove) s;
                    if (mov.target() instanceof IRTemp && mov.source() instanceof IRTemp) {
                            String destname = ((IRTemp) mov.target()).name();
                            String srcname = ((IRTemp) mov.target()).name();
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
                                //TODO: one or none are params

                            }

                        }
                    else {
                        //TODO: one is a temp
                    }
                    }
                    //TODO: binop, anything else with temp
                else {
                    //TODO: generic visit function for statements
                   // instrs.addAll(visit(s));
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
