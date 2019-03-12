package ast;
import edu.cornell.cs.cs4120.xic.ir.*;
import edu.cornell.cs.cs4120.xic.ir.IRBinOp.OpType;
import polyglot.util.Pair;
import symboltable.TypeSymTableFunc;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;
import java.util.stream.Collectors;

public class VisitorTranslation implements VisitorAST<IRNode> {
    private static final int WORD_NUM_BYTES = 8;
    private int labelcounter;
    private int tempcounter;
    private int argcounter;
    private boolean optimize;
    private IRTemp RV;

    private String newLabel() {
        return String.format("l%d", (labelcounter++));
    }

    private String newTemp() {
        return String.format("_mir_t%d", (tempcounter++));
    }

    public VisitorTranslation(boolean opt) {
        this.labelcounter = 0;
        this.tempcounter = 0;
        RV = new IRTemp("RV");
        this.optimize = opt;
    }

    private String returnTypeName(TypeT type){
        if (type instanceof TypeTList){
            TypeTList tuple = (TypeTList) type;
            ArrayList<String> types = new ArrayList<>();
            tuple.getTTauList().forEach((t) -> types.add(returnTypeName(t)));
            return "t" + tuple.getLength() + String.join("",types);
        } else if (type instanceof TypeTUnit) { //TypeTUnit
            return "p";
        } else {
            return typeName(type);
        }
    }

    private String typeName(TypeT type){
        if (type instanceof TypeTList){
            TypeTList tuple = (TypeTList) type;
            ArrayList<String> types = new ArrayList<>();
            tuple.getTTauList().forEach((t) -> types.add(typeName(t)));
            return String.join("",types);
        } else if (type instanceof TypeTTauArray) {
            TypeTTauArray a = (TypeTTauArray) type;
            return "a"+typeName(a.getTypeTTau());
        } else if (type instanceof TypeTTauInt) {
            return "i";
        } else if (type instanceof TypeTTauBool) {
            return "b";
        } else if  (type instanceof TypeTUnit){
            return "";
        } else {
            throw new IllegalArgumentException("invalid type");
        }
    }

    String functionName(String name, TypeSymTableFunc signature){
        String newName = name.replaceAll("_","__");
        String returnType = returnTypeName(signature.getOutput());
        String inputType = typeName(signature.getInput());
        return "_I" + newName + "_" + returnType + inputType;
    }

    private IRStmt conditionalTranslate(Expr e, IRLabel t, IRLabel f){
        if (e instanceof ExprBoolLiteral){ // C[true/false, t, f]
            boolean val = ((ExprBoolLiteral) e).getValue();
            return new IRJump(new IRName(val ? t.name() : f.name()));
        } else if (e instanceof ExprBinop){
            ExprBinop eb = (ExprBinop) e;
            if (eb.getOp() == Binop.AND){// C[e1 & e2, t, f]
                IRLabel t_ = new IRLabel(newLabel());
                return new IRSeq(
                        conditionalTranslate(eb.getLeftExpr(), t_, f),
                        t_,
                        conditionalTranslate(eb.getRightExpr(), t, f)
                );
            } else if (eb.getOp() == Binop.OR) {// C[e1 | e2, t, f]
                IRLabel f_ = new IRLabel(newLabel());
                return new IRSeq(
                        conditionalTranslate(eb.getLeftExpr(), t, f_),
                        f_,
                        conditionalTranslate(eb.getRightExpr(), t, f)
                );
            }
        } else if (e instanceof ExprUnop) {
            ExprUnop eu = (ExprUnop) e;
            if (eu.getOp() == Unop.NOT) { // C[!e , t, f]
                return conditionalTranslate(eu.getExpr(), f, t);
            }
        }
        // C[e, t, f] default rule
        return new IRCJump((IRExpr) e.accept(this),t.name(), f.name());
    }

    //return stmt that checks array bounds, is used in ESeq for indexing
    private IRStmt checkIndex(IRExpr array, IRExpr index, IRExpr temp_array,
                        IRExpr temp_index) {
        String lt = newLabel();
        String lf = newLabel();
        //array bounds checking - True if invalid
        List<IRStmt> seq = new ArrayList<>();
        IRExpr test = new IRBinOp(OpType.OR,
                new IRBinOp(OpType.LT, temp_index, new IRConst(0)),
                new IRBinOp(OpType.GT, temp_index, new IRMem(
                        new IRBinOp(
                                OpType.ADD,
                                temp_array,
                                new IRConst(-WORD_NUM_BYTES)
                        )
                ))
        );
        return new IRSeq(
                new IRMove(temp_array, array),
                new IRMove(temp_index, index),
                new IRCJump(test, lt, lf),
                new IRLabel(lt),
                new IRExp(new IRCall(new IRName("_xi_out_of_bounds"))),
                new IRLabel(lf)
        );
    }

    /**
     * Allocates memory of size.
     * @param size size of the memory.
     * @return A function call expression.
     */
    private IRCall allocateMem(IRExpr size) {
        return new IRCall(new IRName("_xi_alloc"), size);
    }

    /**
     * Allocates array of size (length+1), of which the address is stored in
     * temporary t. The extra byte, i.e., MEM(t) - 8, is used for storing
     * the length. This means that only length bytes are actually available
     * for array t.
     * temporary t.
     * @param t temporary or memory address.
     * @param eIR length of array to be allocated.
     * @return a list of IR statements for performing this array allocation.
     */
    private List<IRStmt> allocateArray(IRExpr t, IRExpr eIR) {
        // Copy eIR to a temporary
        IRTemp length = new IRTemp(newTemp());
        IRMove copyLenToTemp = new IRMove(length, eIR);

        IRBinOp numBytesForArray = new IRBinOp(
                OpType.ADD,
                new IRConst(1), // extra byte for storing length
                new IRBinOp(
                        OpType.MUL,
                        length,
                        new IRConst(WORD_NUM_BYTES)
                )
        );

        // Allocate memory, create y
        IRTemp arrayBaseAddress = new IRTemp(newTemp());
        IRMove baseAllocAddress = new IRMove(
                arrayBaseAddress,
                allocateMem(numBytesForArray)
        );

        // MEM(y) <- length
        IRMove storeLength = new IRMove(new IRMem(arrayBaseAddress), length);

        // t <- y + 8
        IRMove zeroIdxAddress = new IRMove(
                t,
                new IRBinOp(
                        OpType.ADD,
                        arrayBaseAddress,
                        new IRConst(WORD_NUM_BYTES)
                )
        );

        return new ArrayList<>(Arrays.asList(
                copyLenToTemp, baseAllocAddress, storeLength, zeroIdxAddress
        ));
    }

    private List<IRStmt> allocateMultiDimArray(IRExpr t, TypeTTauArray arr) {
        TypeTTau innerType = arr.getTypeTTau();
        Expr size = arr.getSize();
        if (size != null) {
            IRExpr sizeIR = (IRExpr) size.accept(this);

            // Create an array of size sizeIR
            List<IRStmt> arrIR = allocateArray(t, sizeIR);

            if (innerType instanceof TypeTTauArray) {
                // innerType is an array, create a while loop to initialize
                // each element of t
                TypeTTauArray itArray = (TypeTTauArray) innerType;
                IRTemp i = new IRTemp(newTemp());   // loop counter

                IRLabel whileStart = new IRLabel(newLabel());
                IRLabel whileEnd = new IRLabel(newLabel());
                IRBinOp whileGuardExit = new IRBinOp(OpType.GEQ, i, sizeIR);

                return Arrays.asList(
                        new IRMove(i, new IRConst(0)),  // i <- 0
                        whileStart, // while loop starts
                        // Go to end if i >= sizeIR
                        new IRCJump(whileGuardExit, whileEnd.name()),
                        // Allocate multi dim array at t + i*8
                        new IRSeq(allocateMultiDimArray(
                                new IRMem(new IRBinOp(
                                        OpType.ADD,
                                        t,
                                        new IRBinOp(
                                                OpType.MUL,
                                                i,
                                                new IRConst(WORD_NUM_BYTES)
                                        )
                                )),
                                itArray)),
                        // i++
                        new IRMove(
                                i,
                                new IRBinOp(OpType.ADD, i, new IRConst(1))
                        ),
                        new IRJump(new IRName(whileStart.name())),
                        whileEnd    // while loop ends
                );
            } else {
                return arrIR;
            }
        } else {
            // size == null ==> the inner arrays, if any, are also
            // uninitialized. So just move a 1-word memory location to this temp
            return new ArrayList<>(Arrays.asList(new IRMove(
                    t,
                    allocateMem(new IRConst(WORD_NUM_BYTES))
            )));
        }
    }

    @Override
    public IRExpr visit(ExprBinop node) {
        IRExpr l = (IRExpr) node.getLeftExpr().accept(this);
        IRExpr r = (IRExpr) node.getRightExpr().accept(this);
        Binop op = node.getOp();
        //TODO handle errors
        //constant folding the booleans before they get screwed up
        if (l instanceof IRConst && r instanceof IRConst && optimize) {
            long lval = ((IRConst)l).value();
            long rval = ((IRConst)r).value();
            switch (op) {
                case PLUS: return new IRConst(lval + rval);
                case MINUS: return new IRConst(lval - rval);
                case MULT:  return new IRConst(lval * rval);
                case HI_MULT: return new IRBinOp(OpType.HMUL, l, r); //TODO
                case DIV: return new IRConst(lval / rval);
                case MOD: return new IRConst(lval % rval);
                case EQEQ: return new IRConst((lval == rval) ? 1 : 0);
                case NEQ: return new IRConst((lval != rval) ? 1 : 0);
                case GT: return new IRConst((lval > rval) ? 1 : 0);
                case LT: return new IRConst((lval < rval) ? 1 : 0);
                case GTEQ: return new IRConst((lval >= rval) ? 1 : 0);
                case LTEQ: return new IRConst((lval <= rval) ? 1 : 0);
                case AND: return new IRConst((lval == 1 && rval == 1) ? 1 : 0);
                case OR: return new IRConst((lval == 1 || rval == 1) ? 1 : 0);
                default: throw new IllegalArgumentException("Operation Type of " +
                        "Binop node is invalid");
            }
        } else {
            switch (op) {
                case PLUS: return new IRBinOp(OpType.ADD, l, r);
                case MINUS: return new IRBinOp(OpType.SUB, l, r);
                case MULT:  return new IRBinOp(OpType.MUL, l, r);
                case HI_MULT: return new IRBinOp(OpType.HMUL, l, r);
                case DIV: return new IRBinOp(OpType.DIV, l, r);
                case MOD: return new IRBinOp(OpType.MOD, l, r);
                case EQEQ: return new IRBinOp(OpType.EQ, l, r);
                case NEQ: return new IRBinOp(OpType.NEQ, l, r);
                case GT: return new IRBinOp(OpType.GT, l, r);
                case LT: return new IRBinOp(OpType.LT, l, r);
                case GTEQ: return new IRBinOp(OpType.GEQ, l, r);
                case LTEQ: return new IRBinOp(OpType.LEQ, l, r);
                case AND:
                case OR:
                    String l1 = newLabel();
                    String l2 = newLabel();
                    String l3 = newLabel();
                    String x = newTemp();
                    if (op == Binop.AND){
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

                default: throw new IllegalArgumentException("Operation Type of " +
                        "Binop node is invalid");
            }
        }
    }

    @Override
    public IRExpr visit(ExprBoolLiteral node) {
        return new IRConst(node.getValue() ? 1 : 0);
    }

    @Override
    public IRExpr visit(ExprFunctionCall node) {
        String funcName = functionName(node.getName(), node.getSignature());
        ArrayList<IRExpr> args = new ArrayList<>();
        for (Expr e : node.getArgs()){
            args.add((IRExpr) e.accept(this));
        }
        return new IRCall(new IRName(funcName), args);
    }

    @Override
    public IRExpr visit(ExprId node) {
        return new IRTemp(node.getName());
    }

    @Override
    public IRNode visit(ExprIndex node) {
        IRExpr idx = (IRExpr) node.getIndex().accept(this);
        IRExpr array = (IRExpr) node.getArray().accept(this);
        IRTemp t_a = new IRTemp(newTemp());
        IRTemp t_i = new IRTemp(newTemp());
        IRExpr offset = new IRBinOp(
                OpType.MUL,
                new IRConst(WORD_NUM_BYTES),
                t_i
        );
        IRMem access =  new IRMem(new IRBinOp(
                OpType.ADD,
                t_a,
                offset
        ));
        return new IRESeq(checkIndex(array, idx, t_a, t_i), access);
    }

    @Override
    public IRExpr visit(ExprIntLiteral node) {
        return new IRConst(node.getValue());
    }

    @Override
    public IRExpr visit(ExprLength node) {
        return new IRMem(new IRBinOp(
                OpType.ADD,
                (IRExpr) node.getArray().accept(this),
                new IRConst(-WORD_NUM_BYTES)
        ));
    }

    @Override
    public IRExpr visit(ExprArrayLiteral node) {
        IRTemp t = new IRTemp(newTemp());
        List<Expr> contents = node.getContents();
        int length = contents.size();
        List<IRStmt> seq = allocateArray(t, new IRConst(length));
        //store contents
        int offset = 0;
        for (Expr e : contents) {
            IRExpr e_trans = (IRExpr) e.accept(this);
            if (offset == 0) {
                seq.add(new IRMove(t, e_trans));
            } else {
                seq.add(new IRMove(
                        new IRBinOp(
                                OpType.ADD,
                                t,
                                new IRConst(WORD_NUM_BYTES * offset)
                        ),
                        e_trans
                ));
            }
            offset++;
        }
        return new IRESeq(new IRSeq(seq), t);
    }

    @Override
    public IRExpr visit(ExprUnop node) {
        IRExpr e = (IRExpr) node.getExpr().accept(this);
        Unop op = node.getOp();
        switch (op) {
            //NOT(e)  -> XOR(1,e)
            case NOT:
                if (e instanceof IRConst && optimize){
                    long e_val = ((IRConst) e).value();
                    return new IRConst((e_val == 0) ? 1 : 0);
                }
                return new IRBinOp(OpType.XOR, new IRConst(1), e);
            //UMINUS(e) -> SUB(0, e)
            case UMINUS:
                if (e instanceof IRConst && optimize){
                    long e_val = ((IRConst) e).value();
                    return new IRConst(0 - e_val);
                }
                return new IRBinOp(OpType.SUB, new IRConst(0), e);
            default: throw new IllegalArgumentException("Operation Type of " +
                    "Unop node is invalid");
        }
    }

    @Override
    public IRExpr visit(AssignableIndex node) {
        //same as ExprIndex without the MEM because we just want the location
        ExprIndex idx_expr = (ExprIndex) node.getIndex();
        
        IRExpr idx = (IRExpr) idx_expr.getIndex().accept(this);
        IRExpr array = (IRExpr) idx_expr.accept(this);
        IRTemp t_a = new IRTemp(newTemp());
        IRTemp t_i = new IRTemp(newTemp());
        IRExpr offset = new IRBinOp(
                OpType.MUL,
                new IRConst(WORD_NUM_BYTES),
                t_i
        );
        IRExpr location =  new IRBinOp(
                OpType.ADD,
                t_a,
                offset
        );
        return new IRESeq(checkIndex(array, idx, t_a, t_i), location);
    }

    @Override
    public IRNode visit(AssignableId node) {
        ExprId id = node.getExprId();
        return id.accept(this);
    }

    @Override
    public IRNode visit(StmtReturn node) {
        List<Expr> returnVals = node.getReturnVals();
        switch (returnVals.size()) {
            case 0: return new IRReturn();
            case 1:
                IRExpr irReturnVal = (IRExpr) returnVals.get(0).accept(this);
                return new IRSeq(
                        new IRMove(RV, irReturnVal),
                        new IRReturn()
                );
            default:
                // Handle multiple return values
                // TODO: handle multiple return values
                return null;
        }
    }

    @Override
    public IRNode visit(StmtAssign node) {
        return new IRMove(
                (IRExpr) node.getLhs().accept(this),
                (IRExpr) node.getRhs().accept(this)
        );
    }

    @Override
    public IRStmt visit(StmtDecl node) {
        Pair<String, TypeTTau> decl = node.getDecl().getPair();
        String declName = decl.part1();
        TypeTTau declType = decl.part2();
        if (declType instanceof TypeTTauArray) {
            TypeTTauArray declArray = (TypeTTauArray) declType;
            return new IRSeq(
                    allocateMultiDimArray(new IRTemp(declName), declArray)
            );
        } else {
            // declType either an int or bool, allocate one word
            return new IRMove(
                    new IRTemp(declName),
                    allocateMem(new IRConst(WORD_NUM_BYTES))
            );
        }
    }

    @Override
    public IRNode visit(StmtDeclAssign node) {
        return null;
    }

    @Override
    public IRNode visit(StmtProcedureCall node) {
        String funcName = functionName(node.getName(), node.getSignature());
        ArrayList<IRExpr> args = new ArrayList<>();
        for (Expr e : node.getArgs()){
            args.add((IRExpr) e.accept(this));
        }
        return new IRExp(new IRCall(new IRName(funcName), args));
    }

    @Override
    public IRStmt visit(StmtIf node) {
        IRStmt stmt = (IRStmt) node.getThenStmt().accept(this);
        IRLabel lt = new IRLabel(newLabel());
        IRLabel lf = new IRLabel(newLabel());
        IRStmt condition = conditionalTranslate(node.getGuard(), lt, lf);
        return new IRSeq(condition, lt, stmt, lf);
    }

    @Override
    public IRStmt visit(StmtIfElse node) {
        IRStmt stmt = (IRStmt) node.getThenStmt().accept(this);
        IRLabel lt = new IRLabel(newLabel());
        IRLabel lf = new IRLabel(newLabel());
        IRLabel lfin = new IRLabel(newLabel());
        IRStmt condition = conditionalTranslate(node.getGuard(), lt, lf);
        IRJump jmp = new IRJump(new IRName(lfin.name()));
        return new IRSeq(condition, lt, stmt, jmp, lf, lfin);
    }

    @Override
    public IRStmt visit(StmtWhile node) {
        IRStmt stmt = (IRStmt) node.getDoStmt().accept(this);
        IRLabel lh = new IRLabel(newLabel());
        IRLabel lt = new IRLabel(newLabel());
        IRLabel le = new IRLabel(newLabel());
        IRStmt condition = conditionalTranslate(node.getGuard(), lt, le);
        IRJump jmp = new IRJump(new IRName(lh.name()));
        return new IRSeq(lh, condition, lt, stmt, jmp, le);
    }

    @Override
    public IRStmt visit(StmtBlock node) {
        return new IRSeq(node.getStatments().stream()
                .map(s -> (IRStmt) s.accept(this))
                .collect(Collectors.toList())
        );
    }

    @Override
    public IRNode visit(FileProgram node) {
        // TODO: are we sure that we must ignore the imports?
//        List<IRStmt> result = node.getImports().stream()
//                .map(u -> (IRStmt) u.accept(this))
//                .collect(Collectors.toList());
        return new IRSeq(node.getFuncDefns().stream()
                .map(f -> (IRStmt) f.accept(this))
                .collect(Collectors.toList()));
    }

    @Override
    public IRNode visit(FileInterface node) {
        return new IRSeq(node.getFuncDecls().stream()
                .map(f -> (IRStmt) f.accept(this))
                .collect(Collectors.toList())
        );
    }

    @Override
    public IRStmt visit(FuncDefn node) {
        // TODO: where do the arguments go?
        IRStmt funcLabel = new IRLabel(node.getName());
        IRStmt bodyIR = (IRStmt) node.getBody().accept(this);
        return new IRSeq(funcLabel, bodyIR);
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
