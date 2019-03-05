package ast;
import edu.cornell.cs.cs4120.xic.ir.*;

public class VisitorTranslation implements VisitorAST<IRNode> {
    int labelcounter;

    public VisitorTranslation() {
        this.labelcounter = 0;
    }

    @Override
    public IRNode visit(ExprBinop node) {
        return null;
    }

    @Override
    public IRNode visit(ExprBoolLiteral node) {
        return null;
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
        return null;
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
        return null;
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
        return null;
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
        return null;
    }

    @Override
    public IRNode visit(StmtIfElse node) {
        return null;
    }

    @Override
    public IRNode visit(StmtWhile node) {
        return null;
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
