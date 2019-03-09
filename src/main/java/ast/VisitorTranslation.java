package ast;
import edu.cornell.cs.cs4120.xic.ir.*;
import edu.cornell.cs.cs4120.xic.ir.IRBinOp.OpType;
import edu.cornell.cs.cs4120.xic.ir.IRMem.MemType;
import symboltable.TypeSymTableFunc;
import java.util.ArrayList;
import java.util.List;

public class VisitorTranslation implements VisitorAST<IRNode> {
    private int labelcounter;
    private int tempcounter;
    private IRTemp RV;

    private String newLabel() {
        return String.format("l%d", (labelcounter++));
    }

    private String newTemp() {
        return String.format("_t%d", (tempcounter++));
    }

    public VisitorTranslation() {
        this.labelcounter = 0;
        this.tempcounter = 0;
        RV = new IRTemp("RV");
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

    private String functionName(String name, TypeSymTableFunc signature){
        String newName = name.replaceAll("_","__");
        String returnType = returnTypeName(signature.getOutput());
        String inputType = typeName(signature.getInput());
        return "_I" + newName + "_" + returnType + inputType;
    }

    @Override
    public IRExpr visit(ExprBinop node) {
        IRExpr l = (IRExpr) node.getLeftExpr().accept(this);
        IRExpr r = (IRExpr) node.getRightExpr().accept(this);
        Binop op = node.getOp();
        //constant folding the booleans before they get screwed up
        if (l instanceof IRConst && r instanceof IRConst) {
            long lval = ((IRConst)l).value();
            long rval = ((IRConst)l).value();
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

    //return stmt that checks array bounds, is used in ESeq for indexing
    public IRStmt checkIndex(IRExpr array, IRExpr index, IRExpr temp_array, IRExpr temp_index) {
        String lt = newLabel();
        String lf = newLabel();
        //array bounds checking - True if invalid
        List<IRStmt> seq = new ArrayList();
        IRExpr test = new IRBinOp(OpType.OR,
                new IRBinOp(OpType.LT, temp_index, new IRConst(0)),
                new IRBinOp(OpType.GT, temp_index, new IRMem(
                        new IRBinOp(
                                OpType.ADD,
                                temp_array,
                                new IRConst(-8)
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

    @Override
    public IRNode visit(ExprIndex node) {
        IRExpr idx = (IRExpr) node.getIndex().accept(this);
        IRExpr array = (IRExpr) node.getArray().accept(this);
        IRTemp t_a = new IRTemp(newTemp());
        IRTemp t_i = new IRTemp(newTemp());
        IRExpr offset = new IRBinOp(
                OpType.MUL,
                new IRConst(8),
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
                new IRConst(-8)
        ));
    }

    @Override
    public IRExpr visit(ExprArrayLiteral node) {
        IRTemp t = new IRTemp(newTemp());
        List<Expr> contents = node.getContents();
        int length = contents.size();

        //allocate memory and get 0th index of array
        IRExpr alloc = new IRCall(
                new IRName("_xi_alloc"),
                new IRBinOp(
                        OpType.MUL,
                        new IRConst(length + 1), //extra mem to store length
                        new IRConst(8)
                )
        );
        IRExpr idx_0 = new IRBinOp(OpType.ADD, new IRConst(8), alloc);

        List<IRStmt> seq = new ArrayList();
        //assign 0-index to temp
        seq.add(new IRMove(t, idx_0));
        //store length
        seq.add(new IRMove(
                new IRBinOp(OpType.SUB, t, new IRConst(8)),
                new IRConst(length)
                ));

        //store contents
        int offset = 0;
        for (Expr e : contents) {
            IRExpr e_trans = (IRExpr) e.accept(this);
            if (offset == 0) {
                seq.add(new IRMove(idx_0, e_trans));
            } else {
                seq.add(new IRMove(
                        new IRBinOp(OpType.ADD, t, new IRConst(8 * offset)),
                        e_trans
                ));
            }
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
                if (e instanceof IRConst){
                    long e_val = ((IRConst) e).value();
                    return new IRConst((e_val == 0) ? 1 : 0);
                }
                return new IRBinOp(OpType.XOR, new IRConst(1), e);
            //UMINUS(e) -> SUB(0, e)
            case UMINUS:
                if (e instanceof IRConst){
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
                new IRConst(8),
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
    public IRNode visit(AssignableExpr node) {
        return null;//TODO
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
        return null;
    }

    @Override
    public IRNode visit(StmtDecl node) {
        return null;
    }

    @Override
    public IRNode visit(StmtDeclAssign node) {
        return null;
    }

    @Override
    public IRNode visit(StmtProcedureCall node) {
        return null;
    }

    @Override
    public IRNode visit(StmtIf node) {
        IRExpr condition = (IRExpr) node.getGuard().accept(this);
        IRStmt stmt = (IRStmt) node.getThenStmt().accept(this);
        IRLabel lt = new IRLabel(newLabel());
        IRLabel lf = new IRLabel(newLabel());
        IRCJump jmp = new IRCJump(condition, lt.name(), lf.name());
        return new IRSeq(jmp, lt, stmt, lf);
    }

    @Override
    public IRNode visit(StmtIfElse node) {
        IRExpr condition = (IRExpr) node.getGuard().accept(this);
        IRStmt stmt = (IRStmt) node.getThenStmt().accept(this);
        IRLabel lt = new IRLabel(newLabel());
        IRLabel lf = new IRLabel(newLabel());
        IRLabel lfin = new IRLabel(newLabel());
        IRCJump cjmp = new IRCJump(condition, lt.name(), lf.name());
        IRJump jmp = new IRJump(new IRName(lfin.name()));
        return new IRSeq(cjmp, lt, stmt, jmp, lf, lfin);
    }

    @Override
    public IRNode visit(StmtWhile node) {
        IRExpr condition = (IRExpr) node.getGuard().accept(this);
        IRStmt stmt = (IRStmt) node.getDoStmt().accept(this);
        IRLabel lh = new IRLabel(newLabel());
        IRLabel lt = new IRLabel(newLabel());
        IRLabel le = new IRLabel(newLabel());
        IRCJump cjmp = new IRCJump(condition, le.name(), lt.name());
        IRJump jmp = new IRJump(new IRName(lh.name()));
        return new IRSeq(lh, cjmp, lt, stmt, jmp, le);
    }

    @Override
    public IRNode visit(StmtBlock node) {
        return null;
    }

    @Override
    public IRNode visit(FileProgram node) {
        return null;
    }

    @Override
    public IRNode visit(FileInterface node) {
        return null;
    }

    @Override
    public IRNode visit(FuncDefn node) {
        return null;
    }

    @Override
    public IRNode visit(FuncDecl node) {
        return null;
    }

    @Override
    public IRNode visit(UseInterface node) {
        return null;
    }
}
