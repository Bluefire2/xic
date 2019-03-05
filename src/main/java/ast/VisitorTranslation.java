package ast;
import edu.cornell.cs.cs4120.xic.ir.*;
import edu.cornell.cs.cs4120.xic.ir.IRBinOp.OpType;

public class VisitorTranslation implements VisitorAST<IRNode> {
    private int labelcounter;
    private IRTemp RV;

    private String newLabel() {
        return String.format("l%d", (labelcounter++));
    }

    public VisitorTranslation() {
        this.labelcounter = 0;
        RV = new IRTemp("RV");
    }

    @Override
    public IRNode visit(ExprBinop node) {
        IRExpr l = (IRExpr) node.getLeftExpr().accept(this);
        IRExpr r = (IRExpr) node.getRightExpr().accept(this);
        Binop op = node.getOp();
        switch (op) {
            case PLUS: return new IRBinOp(OpType.ADD, l, r);
            case MINUS: return new IRBinOp(OpType.SUB, l, r);
            case MULT: return new IRBinOp(OpType.MUL, l, r);
            case HI_MULT: return new IRBinOp(OpType.HMUL, l, r);
            case DIV: return new IRBinOp(OpType.DIV, l, r);
            case MOD: return new IRBinOp(OpType.MOD, l, r);
            case EQEQ: return new IRBinOp(OpType.EQ, l, r);
            case NEQ: return new IRBinOp(OpType.NEQ, l, r);
            case GT: return new IRBinOp(OpType.GT, l, r);
            case LT: return new IRBinOp(OpType.LT, l, r);
            case GTEQ: return new IRBinOp(OpType.GEQ, l, r);
            case LTEQ: return new IRBinOp(OpType.LEQ, l, r);
            //need to translate if/while guards specially
            case AND: return new IRBinOp(OpType.AND, l, r);
            case OR: return new IRBinOp(OpType.OR, l, r);
            default: throw new IllegalArgumentException("Operation Type of " +
                    "Binop node is invalid");
        }
    }

    @Override
    public IRNode visit(ExprBoolLiteral node) {
        return new IRConst(node.getValue() ? 1 : 0);
    }

    @Override
    public IRNode visit(ExprFunctionCall node) {
        return null;
    }

    @Override
    public IRNode visit(ExprId node) {
        return null;
    }

    @Override
    public IRNode visit(ExprIndex node) {
        return null;
    }

    @Override
    public IRNode visit(ExprIntLiteral node) {
        return new IRConst(node.getValue());
    }

    @Override
    public IRNode visit(ExprLength node) {
        return null;
    }

    @Override
    public IRNode visit(ExprArrayLiteral node) {
        return null;
    }

    @Override
    public IRNode visit(ExprUnop node) {
        IRExpr e = (IRExpr) node.getExpr().accept(this);
        Unop op = node.getOp();
        switch (op) {
            //NOT(e)  -> AND(False, e)
            case NOT: return new IRBinOp(OpType.AND, new IRConst(0), e);
            //UMINUS(e) -> SUB(0, e)
            case UMINUS: return new IRBinOp(OpType.SUB, new IRConst(0), e);
            default: throw new IllegalArgumentException("Operation Type of " +
                    "Unop node is invalid");
        }
    }

    @Override
    public IRNode visit(AssignableIndex node) {
        return null;
    }

    @Override
    public IRNode visit(AssignableUnderscore node) {
        return null;
    }

    @Override
    public IRNode visit(AssignableExpr node) {
        return null;
    }

    @Override
    public IRNode visit(StmtReturn node) {
        if (node.getReturnVals().isEmpty()) {
            return new IRReturn();
        } else if (node.getReturnVals().size() == 1) {
            IRExpr returnValue = (IRExpr) node.getReturnVals().get(0).accept(this);
            IRMove mov = new IRMove(RV, returnValue);
            return new IRSeq(mov, new IRReturn());
        } else {
            //TODO: handle multiple return values
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
