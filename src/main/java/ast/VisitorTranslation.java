package ast;

import edu.cornell.cs.cs4120.xic.ir.*;
import edu.cornell.cs.cs4120.xic.ir.IRBinOp.OpType;
import polyglot.util.Pair;
import symboltable.TypeSymTableFunc;
import edu.cornell.cs.cs4120.util.InternalCompilerError;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;
import java.util.stream.Collectors;

public class VisitorTranslation implements VisitorAST<IRNode> {
    private static final int WORD_NUM_BYTES = 8;
    private int labelcounter;
    private int tempcounter;
    private boolean optimize;
    private String name;

    private String newLabel() {
        return String.format("_mir_l%d", (labelcounter++));
    }

    private String newTemp() {
        return String.format("_mir_t%d", (tempcounter++));
    }

    public VisitorTranslation(boolean opt, String name) {
        this.labelcounter = 0;
        this.tempcounter = 0;
        this.optimize = opt;
        this.name = name; //name of the comp unit
    }

    private String returnValueName(int i) {
        return "_RET" + i;
    }

    private String funcArgName(int i) {
        return "_ARG" + i;
    }

    private String returnTypeName(TypeT type) {
        if (type instanceof TypeTList) {
            TypeTList tuple = (TypeTList) type;
            ArrayList<String> types = new ArrayList<>();
            tuple.getTTauList().forEach((t) -> types.add(returnTypeName(t)));
            return "t" + tuple.getLength() + String.join("", types);
        } else if (type instanceof TypeTUnit) { //TypeTUnit
            return "p";
        } else {
            return typeName(type);
        }
    }

    private String typeName(TypeT type) {
        if (type instanceof TypeTList) {
            TypeTList tuple = (TypeTList) type;
            ArrayList<String> types = new ArrayList<>();
            tuple.getTTauList().forEach((t) -> types.add(typeName(t)));
            return String.join("", types);
        } else if (type instanceof TypeTTauArray) {
            TypeTTauArray a = (TypeTTauArray) type;
            return "a" + typeName(a.getTypeTTau());
        } else if (type instanceof TypeTTauInt) {
            return "i";
        } else if (type instanceof TypeTTauBool) {
            return "b";
        } else if (type instanceof TypeTUnit) {
            return "";
        } else {
            throw new IllegalArgumentException("invalid type");
        }
    }

    String functionName(String name, TypeSymTableFunc signature) {
        String newName = name.replaceAll("_", "__");
        String returnType = returnTypeName(signature.getOutput());
        String inputType = typeName(signature.getInput());
        return "_I" + newName + "_" + returnType + inputType;
    }

    private IRStmt conditionalTranslate(Expr e, String labelt, String labelf) {
        if (e instanceof ExprBoolLiteral) { // C[true/false, t, f]
            boolean val = ((ExprBoolLiteral) e).getValue();
            return new IRJump(new IRName(val ? labelt : labelf));
        } else if (e instanceof ExprBinop) {
            ExprBinop eb = (ExprBinop) e;
            if (eb.getOp() == Binop.AND) {// C[e1 & e2, t, f]
                String t_ = newLabel();
                return new IRSeq(
                        conditionalTranslate(eb.getLeftExpr(), t_, labelf),
                        new IRLabel(t_),
                        conditionalTranslate(eb.getRightExpr(), labelt, labelf)
                );
            } else if (eb.getOp() == Binop.OR) {// C[e1 | e2, t, f]
                String f_ = newLabel();
                return new IRSeq(
                        conditionalTranslate(eb.getLeftExpr(), labelt, f_),
                        new IRLabel(f_),
                        conditionalTranslate(eb.getRightExpr(), labelt, labelf)
                );
            }
        } else if (e instanceof ExprUnop) {
            ExprUnop eu = (ExprUnop) e;
            if (eu.getOp() == Unop.NOT) { // C[!e , t, f]
                return conditionalTranslate(eu.getExpr(), labelf, labelt);
            }
        }
        // C[e, t, f] default rule
        return new IRCJump((IRExpr) e.accept(this), labelt, labelf);
    }

    //return stmt that checks array bounds, is used in ESeq for indexing
    private IRStmt checkIndex(IRExpr array, IRExpr index, String temp_array,
                              String temp_index) {
        String lt = newLabel();
        String lf = newLabel();
        //array bounds checking - True if invalid
        IRExpr test = new IRBinOp(OpType.OR,
                new IRBinOp(OpType.LT, new IRTemp(temp_index), new IRConst(0)),
                new IRBinOp(OpType.GEQ, new IRTemp(temp_index), new IRMem(
                        new IRBinOp(
                                OpType.SUB,
                                new IRTemp(temp_array),
                                new IRConst(WORD_NUM_BYTES)
                        )
                ))
        );
        return new IRSeq(
                new IRMove(new IRTemp(temp_array), array),
                new IRMove(new IRTemp(temp_index), index),
                new IRCJump(test, lt, lf),
                new IRLabel(lt),
                new IRExp(new IRCall(new IRName("_xi_out_of_bounds"))),
                new IRLabel(lf)
        );
    }

    /**
     * Allocates memory of size.
     *
     * @param size size of the memory.
     * @return A function call expression.
     */
    private IRCall allocateMem(IRExpr size) {
        return new IRCall(new IRName("_xi_alloc"), size);
    }

    /**
     * Allocates array of size (length+1), of which the address is stored in
     * temporary t. The extra word, i.e., MEM(t) - 64, is used for storing
     * the length. This means that only length bytes are actually available
     * for array t.
     *
     * @param t   temporary or memory address.
     * @param eIR length of array to be allocated.
     * @return a list of IR statements for performing this array allocation.
     */
    private List<IRStmt> allocateArray(IRExpr t, IRExpr eIR) {
        assert t instanceof IRTemp || t instanceof IRMem;

        // Copy eIR to a temporary
        String length = newTemp();
        IRMove copyLenToTemp = new IRMove(new IRTemp(length), eIR);

        IRBinOp numBytesForArray = new IRBinOp(
                OpType.ADD,
                new IRConst(WORD_NUM_BYTES), // extra word for storing length
                new IRBinOp(
                        OpType.MUL,
                        new IRTemp(length),
                        new IRConst(WORD_NUM_BYTES)
                )
        );

        // Allocate memory, create y
        String arrayBaseAddress = newTemp();
        IRMove baseAllocAddress = new IRMove(
                new IRTemp(arrayBaseAddress),
                allocateMem(numBytesForArray)
        );

        // MEM(y) <- length
        IRMove storeLength = new IRMove(new IRMem(new IRTemp(arrayBaseAddress)), new IRTemp(length));

        // t <- y + 8
        IRMove zeroIdxAddress = new IRMove(
                t,
                new IRBinOp(
                        OpType.ADD,
                        new IRTemp(arrayBaseAddress),
                        new IRConst(WORD_NUM_BYTES)
                )
        );

        return new ArrayList<>(Arrays.asList(
                copyLenToTemp, baseAllocAddress, storeLength, zeroIdxAddress
        ));
    }

    /**
     * Allocates a multi dimensional array of type arr starting at
     * temporary/memory location t.
     *
     * @param t   temporary or memory address.
     * @param arr type of multi dim array.
     * @return a list of IR statements for performing this array allocation.
     */
    private List<IRStmt> allocateMultiDimArray(IRExpr t, TypeTTauArray arr) {
        assert t instanceof IRTemp || t instanceof IRMem;

        TypeTTau innerType = arr.getTypeTTau();
        Expr size = arr.getSize();
        if (size != null) {
            IRExpr sizeIR = (IRExpr) size.accept(this);
            String sizeTemp = newTemp();

            // Create an array of size sizeIR
            List<IRStmt> arrIR = allocateArray(t, sizeIR);

            if (innerType instanceof TypeTTauArray) {
                // innerType is an array, create a while loop to initialize
                // each element of t
                TypeTTauArray itArray = (TypeTTauArray) innerType;
                String i = newTemp();   // loop counter

                String whileStart = newLabel();
                String whileBody = newLabel();
                String whileEnd = newLabel();
                IRBinOp whileGuardExit = new IRBinOp(OpType.GEQ, new IRTemp(i), new IRTemp(sizeTemp));

                arrIR.add(new IRMove(
                        new IRTemp(sizeTemp),
                        new IRMem(new IRBinOp(
                                OpType.ADD,
                                t,
                                new IRConst(-WORD_NUM_BYTES)
                        ))
                ));   // sizeTemp <- MEM(t - 8)
                arrIR.add(new IRMove(new IRTemp(i), new IRConst(0)));   // i <- 0
                arrIR.add(new IRLabel(whileStart));  // while loop starts
                // Go to end if i >= sizeIR
                arrIR.add(new IRCJump(whileGuardExit, whileEnd, whileBody));
                arrIR.add(new IRLabel(whileBody));
                // Allocate multi dim array at t + i*8
                arrIR.add(new IRSeq(allocateMultiDimArray(
                        new IRMem(new IRBinOp(
                                OpType.ADD,
                                t,
                                new IRBinOp(
                                        OpType.MUL,
                                        new IRTemp(i),
                                        new IRConst(WORD_NUM_BYTES)
                                )
                        )),
                        itArray)));
                // i++
                arrIR.add(new IRMove(
                        new IRTemp(i),
                        new IRBinOp(OpType.ADD, new IRTemp(i), new IRConst(1))
                ));
                arrIR.add(new IRJump(new IRName(whileStart)));
                arrIR.add(new IRLabel(whileEnd));    // while loop ends
            }
            return arrIR;
        } else {
            // size == null ==> the inner arrays, if any, are also
            // uninitialized. So just return an empty list of IRStmts
            // (nothing to initialize)
            return new ArrayList<>();
        }
    }

    /**
     * Moves contents of array arr to temporary/memory location newLoc.
     *
     * @param newLoc   temporary or memory address.
     * @param arr      location of array
     * @param sizeTemp a temp storing the size of the array
     * @return a list of IR statements for performing this array copying.
     */
    private List<IRStmt> copyArray(IRExpr newLoc, IRExpr arr, IRExpr sizeTemp) {

        String i = newTemp();   // loop counter
        String l = newLabel();

        String whileStart = newLabel();
        String whileEnd = newLabel();
        IRBinOp whileGuardExit = new IRBinOp(OpType.GEQ, new IRTemp(i), sizeTemp);
        return new ArrayList<>(Arrays.asList(
                new IRMove(new IRTemp(i), new IRConst(0)),  // i <- 0
                new IRLabel(whileStart), // while loop starts
                // Go to end if i >= sizeIR
                new IRCJump(whileGuardExit, whileEnd, l),
                new IRLabel(l),
                // Allocate multi dim array at t + i*8
                new IRMove(
                        //new location
                        new IRMem(new IRBinOp(
                                OpType.ADD,
                                newLoc,
                                new IRBinOp(
                                        OpType.MUL,
                                        new IRTemp(i),
                                        new IRConst(WORD_NUM_BYTES)
                                )
                        )),
                        //old element
                        new IRMem(new IRBinOp(
                                OpType.ADD,
                                arr,
                                new IRBinOp(
                                        OpType.MUL,
                                        new IRTemp(i),
                                        new IRConst(WORD_NUM_BYTES)
                                )
                        ))),
                // i++
                new IRMove(
                        new IRTemp(i),
                        new IRBinOp(OpType.ADD, new IRTemp(i), new IRConst(1))
                ),
                new IRJump(new IRName(whileStart)),
                new IRLabel(whileEnd)    // while loop ends
        ));
    }

    private IRExpr translateBinop(IRExpr l, IRExpr r, Binop op) {
        //arrays are handled elsewhere
        switch (op) {
            case PLUS:
                return new IRBinOp(OpType.ADD, l, r);
            case MINUS:
                return new IRBinOp(OpType.SUB, l, r);
            case MULT:
                return new IRBinOp(OpType.MUL, l, r);
            case HI_MULT:
                return new IRBinOp(OpType.HMUL, l, r);
            case DIV:
                return new IRBinOp(OpType.DIV, l, r);
            case MOD:
                return new IRBinOp(OpType.MOD, l, r);
            case EQEQ:
                return new IRBinOp(OpType.EQ, l, r);
            case NEQ:
                return new IRBinOp(OpType.NEQ, l, r);
            case GT:
                return new IRBinOp(OpType.GT, l, r);
            case LT:
                return new IRBinOp(OpType.LT, l, r);
            case GTEQ:
                return new IRBinOp(OpType.GEQ, l, r);
            case LTEQ:
                return new IRBinOp(OpType.LEQ, l, r);
            case AND:
            case OR:
                String l1 = newLabel();
                String l2 = newLabel();
                String l3 = newLabel();
                String x = newTemp();
                if (op == Binop.AND) {
                    return new IRESeq(new IRSeq(
                            new IRMove(new IRTemp(x), new IRConst(0)),
                            new IRCJump(l, l1, l3),
                            new IRLabel(l1),
                            new IRCJump(r, l2, l3),
                            new IRLabel(l2),
                            new IRMove(new IRTemp(x), new IRConst(1)),
                            new IRLabel(l3)),
                            new IRTemp(x)
                    );
                } else {
                    return new IRESeq(new IRSeq(
                            new IRMove(new IRTemp(x), new IRConst(1)),
                            new IRCJump(l, l3, l1),
                            new IRLabel(l1),
                            new IRCJump(r, l3, l2),
                            new IRLabel(l2),
                            new IRMove(new IRTemp(x), new IRConst(0)),
                            new IRLabel(l3)),
                            new IRTemp(x)
                    );
                }

            default:
                throw new InternalCompilerError("Invalid binary operation");
        }
    }

    @Override
    public IRExpr visit(ExprBinop node) {
        IRExpr l = (IRExpr) node.getLeftExpr().accept(this);
        IRExpr r = (IRExpr) node.getRightExpr().accept(this);
        Binop op = node.getOp();
        //special case for array concatenation
        if (node.getTypeCheckType() instanceof TypeTTauArray && op == Binop.PLUS) {
            String tempL = newTemp();
            String tempR = newTemp();
            IRMove moveL = new IRMove(new IRTemp(tempL), l);
            IRMove moveR = new IRMove(new IRTemp(tempR), r);

            String tempLLength = newTemp();
            String tempRLength = newTemp();
            IRMove lengthL = new IRMove(new IRTemp(tempLLength), new IRMem(
                    new IRBinOp(OpType.SUB,
                            new IRTemp(tempL),
                            new IRConst(WORD_NUM_BYTES)
                    )));
            IRMove lengthR = new IRMove(new IRTemp(tempRLength), new IRMem(
                    new IRBinOp(OpType.SUB,
                            new IRTemp(tempR),
                            new IRConst(WORD_NUM_BYTES)
                    )));
            String tempNewArray = newTemp();

            //move lengths to temps
            List<IRStmt> stmts = new ArrayList<>(Arrays.asList(
                    moveL,
                    moveR,
                    lengthL,
                    lengthR
            ));
            //make new array
            stmts.addAll(allocateArray(
                    new IRTemp(tempNewArray),
                    new IRBinOp(OpType.ADD,
                            new IRTemp(tempLLength),
                            new IRTemp(tempRLength))
            ));
            //copy left
            stmts.addAll(copyArray(new IRTemp(tempNewArray),
                    new IRTemp(tempL), new IRTemp(tempLLength)));
            //copy right
            stmts.addAll(copyArray(
                    new IRBinOp(OpType.ADD,
                            new IRTemp(tempNewArray),
                            new IRTemp(tempLLength)),
                    new IRTemp(tempR),
                    new IRTemp(tempRLength)
            ));
            IRSeq seq = new IRSeq(stmts);
            return new IRESeq(seq, new IRTemp(tempNewArray));
        }
        //constant folding the booleans before they get screwed up
        if (l instanceof IRConst && r instanceof IRConst && optimize) {
            long lval = ((IRConst) l).value();
            long rval = ((IRConst) r).value();
            switch (op) {
                case PLUS:
                    return new IRConst(lval + rval);
                case MINUS:
                    return new IRConst(lval - rval);
                case MULT:
                    return new IRConst(lval * rval);
                case HI_MULT:
                    return new IRConst(BigInteger.valueOf(lval)
                            .multiply(BigInteger.valueOf(rval))
                            .shiftRight(64)
                            .longValue());
                case DIV:
                    if (rval == 0L) {
                        return translateBinop(l, r, op);
                    }
                    return new IRConst(lval / rval);
                case MOD:
                    if (rval == 0L) {
                        return translateBinop(l, r, op);
                    }
                    return new IRConst(lval % rval);
                case EQEQ:
                    return new IRConst((lval == rval) ? 1 : 0);
                case NEQ:
                    return new IRConst((lval != rval) ? 1 : 0);
                case GT:
                    return new IRConst((lval > rval) ? 1 : 0);
                case LT:
                    return new IRConst((lval < rval) ? 1 : 0);
                case GTEQ:
                    return new IRConst((lval >= rval) ? 1 : 0);
                case LTEQ:
                    return new IRConst((lval <= rval) ? 1 : 0);
                case AND:
                    return new IRConst((lval == 1 && rval == 1) ? 1 : 0);
                case OR:
                    return new IRConst((lval == 1 || rval == 1) ? 1 : 0);
                default:
                    throw new InternalCompilerError("Invalid binary operation");
            }
        } else {
            return translateBinop(l, r, op);
        }
    }

    @Override
    public IRExpr visit(ExprBoolLiteral node) {
        return new IRConst(node.getValue() ? 1 : 0);
    }

    @Override
    public IRExpr visit(ExprFunctionCall node) {
        String funcName = functionName(node.getName(), node.getSignature());
        ArrayList<IRExpr> argsIR = new ArrayList<>();

        for (Expr arg : node.getArgs()) {
            // Add argIR to list of arguments to be passed to IRCall
            argsIR.add((IRExpr) arg.accept(this));
        }
        return new IRCall(new IRName(funcName), argsIR);
    }

    @Override
    public IRExpr visit(ExprId node) {
        return new IRTemp(node.getName());
    }

    @Override
    public IRNode visit(ExprIndex node) {
        IRExpr idx = (IRExpr) node.getIndex().accept(this);
        IRExpr array = (IRExpr) node.getArray().accept(this);
        String t_a = newTemp();
        String t_i = newTemp();
        IRExpr offset = new IRBinOp(
                OpType.MUL,
                new IRConst(WORD_NUM_BYTES),
                new IRTemp(t_i)
        );
        IRMem access = new IRMem(new IRBinOp(
                OpType.ADD,
                new IRTemp(t_a),
                offset
        ));
        return new IRESeq(
                checkIndex(array, idx, t_a, t_i),
                access);
    }

    @Override
    public IRExpr visit(ExprIntLiteral node) {
        return new IRConst(node.getValue());
    }

    @Override
    public IRExpr visit(ExprLength node) {
        return new IRMem(new IRBinOp(
                OpType.SUB,
                (IRExpr) node.getArray().accept(this),
                new IRConst(WORD_NUM_BYTES)
        ));
    }

    @Override
    public IRExpr visit(ExprArrayLiteral node) {
        String t = newTemp();
        List<Expr> contents = node.getContents();
        int length = contents.size();
        List<IRStmt> seq = allocateArray(new IRTemp(t), new IRConst(length));
        //store contents
        int offset = 0;
        for (Expr e : contents) {
            IRExpr e_trans = (IRExpr) e.accept(this);
            if (offset == 0) {
                seq.add(new IRMove(new IRMem(new IRTemp(t)), e_trans));
            } else {
                seq.add(new IRMove(
                        new IRMem(new IRBinOp(
                                OpType.ADD,
                                new IRTemp(t),
                                new IRConst(WORD_NUM_BYTES * offset)
                        )),
                        e_trans
                ));
            }
            offset++;
        }
        return new IRESeq(new IRSeq(seq), new IRTemp(t));
    }

    @Override
    public IRExpr visit(ExprUnop node) {
        IRExpr e = (IRExpr) node.getExpr().accept(this);
        Unop op = node.getOp();
        switch (op) {
            //NOT(e)  -> XOR(1,e)
            case NOT:
                if (e instanceof IRConst && optimize) {
                    long e_val = ((IRConst) e).value();
                    return new IRConst((e_val == 0) ? 1 : 0);
                }
                return new IRBinOp(OpType.XOR, new IRConst(1), e);
            //UMINUS(e) -> SUB(0, e)
            case UMINUS:
                if (e instanceof IRConst && optimize) {
                    long e_val = ((IRConst) e).value();
                    return new IRConst(0 - e_val);
                }
                return new IRBinOp(OpType.SUB, new IRConst(0), e);
            default:
                throw new InternalCompilerError("Invalid unary operation");
        }
    }

    @Override
    public IRExpr visit(AssignableIndex node) {
        //same as ExprIndex without the MEM because we just want the location
        ExprIndex idx_expr = (ExprIndex) node.getIndex();

        IRExpr idx = (IRExpr) idx_expr.getIndex().accept(this);
        IRExpr array = (IRExpr) idx_expr.accept(this);
        String t_a = newTemp();
        String t_i = newTemp();
        IRExpr offset = new IRBinOp(
                OpType.MUL,
                new IRConst(WORD_NUM_BYTES),
                new IRTemp(t_i)
        );
        //TODO does this need to be a mem
        IRExpr location = new IRBinOp(
                OpType.ADD,
                new IRTemp(t_a),
                offset
        );
        return new IRESeq(
                checkIndex(array, idx, t_a, t_i),
                location);
    }

    @Override
    public IRNode visit(AssignableId node) {
        ExprId id = node.getExprId();
        return id.accept(this);
    }

    @Override
    public IRNode visit(StmtReturn node) {
        // Translate each expr into IR and pass it into IRReturn
        return new IRReturn(
                node.getReturnVals().stream()
                        .map(e -> (IRExpr) e.accept(this))
                        .collect(Collectors.toList())
        );
    }

    @Override
    public IRNode visit(StmtAssign node) {
        return new IRMove(
                (IRExpr) node.getLhs().accept(this),
                (IRExpr) node.getRhs().accept(this)
        );
    }

    /**
     * Initializes memory for type declType and assigns the memory to the the
     * temporary variable given by declName.
     *
     * @param declName name of the temporary.
     * @param declType type of the memory to be allocated.
     * @return IR stmts to do this allocation.
     */
    private IRStmt initDecl(String declName, TypeTTau declType) {
        if (declType instanceof TypeTTauArray) {
            TypeTTauArray declArray = (TypeTTauArray) declType;
            return new IRSeq(
                    allocateMultiDimArray(new IRTemp(declName), declArray)
            );
        } else {
            // declType either an int or bool, initialize arbitrarily
            return new IRMove(new IRTemp(declName), new IRConst(0));
        }
    }

    @Override
    public IRStmt visit(StmtDecl node) {
        Pair<String, TypeTTau> decl = node.getDecl().getPair();
        return initDecl(decl.part1(), decl.part2());
    }

    @Override
    public IRNode visit(StmtDeclAssign node) {
        List<TypeDecl> decls = node.getDecls();
        IRExpr rhsIR = (IRExpr) node.getRhs().accept(this);

        List<IRStmt> declsInitIR = new ArrayList<>();
        List<IRStmt> moveRetIR = new ArrayList<>();

        if (node.getRhs() instanceof ExprFunctionCall) {
            for (int i = 0; i < decls.size(); ++i) {
                Pair<String, TypeTTau> decl =
                        ((TypeDeclVar) decls.get(i)).getPair();
                // Initialize the ith declaration
                if (decl.part2() instanceof TypeTTauArray) {
                    declsInitIR.add(initDecl(decl.part1(), decl.part2()));
                }
                // Move return value i to this declaration
                moveRetIR.add(new IRMove(
                        new IRTemp(decl.part1()),
                        new IRTemp(returnValueName(i))
                ));
            }

            return new IRSeq(
                    new IRExp(rhsIR),   // evaluate rhs
                    new IRSeq(declsInitIR), // initialize declarations
                    new IRSeq(moveRetIR)    // move return values of func to decls
            );
        } else {
            // rhs not a function, decls must be size 1
            Pair<String, TypeTTau> decl =
                    ((TypeDeclVar) decls.get(0)).getPair();
            if (decl.part2() instanceof TypeTTauArray) {
                // Add initializing code if necessary
                declsInitIR.add(initDecl(decl.part1(), decl.part2()));
            }
            moveRetIR.add(new IRMove(
                    new IRTemp(decl.part1()),
                    rhsIR
            ));

            return new IRSeq(
                    new IRSeq(declsInitIR), // initialize declarations
                    new IRSeq(moveRetIR)    // move return values of func to decls
            );
        }
    }

    @Override
    public IRNode visit(StmtProcedureCall node) {
        String funcName = functionName(node.getName(), node.getSignature());
        List<IRExpr> argsIR = new ArrayList<>();

        for (Expr arg : node.getArgs()) {
            argsIR.add((IRExpr) arg.accept(this));
        }
        return new IRSeq(new IRExp(new IRCall(new IRName(funcName), argsIR)));
    }

    @Override
    public IRStmt visit(StmtIf node) {
        IRStmt stmt = (IRStmt) node.getThenStmt().accept(this);
        String lt = newLabel();
        String lf = newLabel();
        IRStmt condition = conditionalTranslate(node.getGuard(), lt, lf);
        return new IRSeq(condition, new IRLabel(lt), stmt, new IRLabel(lf));
    }

    @Override
    public IRStmt visit(StmtIfElse node) {
        IRStmt stmtThen = (IRStmt) node.getThenStmt().accept(this);
        IRStmt stmtElse = (IRStmt) node.getElseStmt().accept(this);
        String lt = newLabel();
        String lf = newLabel();
        String lfin = newLabel();
        IRStmt condition = conditionalTranslate(node.getGuard(), lt, lf);
        IRJump jmp = new IRJump(new IRName(lfin));
        return new IRSeq(condition,
                new IRLabel(lt),
                stmtThen,
                jmp,
                new IRLabel(lf),
                stmtElse,
                new IRLabel(lfin)
        );
    }

    @Override
    public IRStmt visit(StmtWhile node) {
        IRStmt stmt = (IRStmt) node.getDoStmt().accept(this);
        String lh = newLabel();
        String lt = newLabel();
        String le = newLabel();
        IRStmt condition = conditionalTranslate(node.getGuard(), lt, le);
        IRJump jmp = new IRJump(new IRName(lh));
        return new IRSeq(
                new IRLabel(lh),
                condition,
                new IRLabel(lt),
                stmt,
                jmp,
                new IRLabel(le));
    }

    @Override
    public IRSeq visit(StmtBlock node) {
        return new IRSeq(node.getStatments().stream()
                .map(s -> (IRStmt) s.accept(this))
                .collect(Collectors.toList())
        );
    }

    @Override
    public IRCompUnit visit(FileProgram node) {
        IRCompUnit program = new IRCompUnit(name);
        for (FuncDefn d : node.getFuncDefns()) {
            program.appendFunc((IRFuncDecl) d.accept(this));
        }
        return program;
    }

    @Override
    public IRNode visit(FileInterface node) {
        return null;
    }

    @Override
    public IRFuncDecl visit(FuncDefn node) {
        String funcName = functionName(
                node.getName(), (TypeSymTableFunc) node.getSignature().part2());

        List<Pair<String, TypeTTau>> params = node.getParams();
        List<IRStmt> moveArgs = new ArrayList<>();
        for (int i = 0; i < params.size(); ++i) {
            // Move argi into params
            moveArgs.add(new IRMove(
                    new IRTemp(params.get(i).part1()),
                    new IRTemp(funcArgName(i))
            ));
        }

        Stmt body = node.getBody();
        IRSeq bodyIR = (IRSeq) body.accept(this);

        // Add a return statement if not already present
        if (body.getTypeCheckType().equals(TypeR.Unit)) {
            bodyIR = new IRSeq(bodyIR, new IRReturn());
        }

        return new IRFuncDecl(funcName, new IRSeq(new IRSeq(moveArgs), bodyIR));
    }

    @Override
    public IRNode visit(FuncDecl node) {
        return null;
    }

    @Override
    public IRStmt visit(UseInterface node) {
        return null;
    }
}
