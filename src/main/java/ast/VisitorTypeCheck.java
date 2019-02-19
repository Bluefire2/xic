package ast;

import symboltable.*;
import java.util.List;

public class VisitorTypeCheck implements VisitorAST {
    private SymbolTable symTable;

    VisitorTypeCheck(SymbolTable symTable){
        this.symTable = symTable;
    }

    public SymbolTable getSymTable() {
        return symTable;
    }

    @Override
    public void visit(BinopExpr node) {
        TypeT lType = node.getLeft().getTypeCheckType();
        TypeT rType = node.getRight().getTypeCheckType();

        boolean lTypeIsInt = lType instanceof TypeTTauInt;
        boolean lTypeIsBool = lType instanceof TypeTTauBool;
        boolean lTypeIsArray = lType instanceof TypeTTauArray;
        boolean rTypeIsInt = rType instanceof TypeTTauInt;
        boolean rTypeIsBool = rType instanceof TypeTTauBool;
        boolean rTypeIsArray = rType instanceof TypeTTauArray;

        switch (node.getOp()) {
            case PLUS:
            case MINUS:
            case MULT:
            case HI_MULT:
            case DIV:
            case MOD:
                if (lTypeIsInt && rTypeIsInt) {
                    node.setTypeCheckType(new TypeTTauInt());
                    break;
                }
                //TODO: throw error with position
            case EQEQ:
            case NEQ:
                if ((lTypeIsInt && rTypeIsInt) || (lTypeIsBool && rTypeIsBool)) {
                    node.setTypeCheckType(new TypeTTauBool());
                    break;
                }
                if (lTypeIsArray && rTypeIsArray) {
                    TypeTTauArray lTau = (TypeTTauArray) lType;
                    TypeTTauArray rTau = (TypeTTauArray) rType;
                    if (lTau.getTypeTTau().equals(rTau.getTypeTTau())) {
                        node.setTypeCheckType(new TypeTTauBool());
                        break;
                    }
                }
                // TODO: throw error with position
            case GT:
            case LT:
            case GTEQ:
            case LTEQ:
                if (lTypeIsInt && rTypeIsInt) {
                    node.setTypeCheckType(new TypeTTauBool());
                    break;
                }
                // TODO: throw error with position
            case AND:
            case OR:
                if (lTypeIsBool && rTypeIsBool) {
                    node.setTypeCheckType(new TypeTTauBool());
                    break;
                }
                //TODO: throw error with position
            default:
                throw new IllegalArgumentException("Operation Type of " +
                        "Binop node is invalid");
        }
    }

    @Override
    public void visit(BoolLiteralExpr node) {
        node.setTypeCheckType(new TypeTTauBool());
    }

    @Override
    public void visit(FunctionCallExpr node) {
        //TODO: I think we need a meta-type otherwise cannot set type to a list
        String name = node.getName();
        try {
            TypeSymTable t = symTable.lookup(name);
            if (t instanceof TypeSymTableFunc) {
                TypeT inTypes = ((TypeSymTableFunc) t).getInput();
                TypeT outTypes = ((TypeSymTableFunc) t).getOutput();
                // TODO: incomplete
            } else {
                //TODO: throw error
            }
        } catch (NotFoundException e){
            //TODO: handle exception
        }
    }

    @Override
    public void visit(IdExpr node) {
        String name = node.getName();
        try {
            TypeSymTable t = symTable.lookup(name);
            if (t instanceof TypeSymTableVar) {
                node.setTypeCheckType(((TypeSymTableVar) t).getTypeTTau());
            } else {
                //TODO: throw error
            }
        } catch (NotFoundException e){
            //TODO: handle exception
        }
    }

    @Override
    public void visit(IndexExpr node) {
        Expr array = node.getArray();
        Expr idx = node.getIndex();
        TypeT at = array.getTypeCheckType();
        TypeT it = idx.getTypeCheckType();
        if (at instanceof TypeTTauArray && it instanceof TypeTTauInt) {
            node.setTypeCheckType(((TypeTTauArray) at).getTypeTTau());
        } else {
            //TODO: throw error
        }
    }

    @Override
    public void visit(IntLiteralExpr node) {
        node.setTypeCheckType(new TypeTTauInt());
    }

    @Override
    public void visit(LengthExpr node) {
        if (node.getTypeCheckType() instanceof TypeTTauArray){
            node.setTypeCheckType(new TypeTTauInt());
        } else {
            //TODO: throw error
        }
    }

    @Override
    public void visit(ArrayLiteralExpr node) { //TODO: handle lengths somehow
        List<Expr> contents = node.getContents();
        int length = node.getLength();
        if (length == 0) {
            // TODO: this makes the tau type of this array node null,
            //  potentially resulting in unchecked NullPointerExceptions
            node.setTypeCheckType(new TypeTTauArray());
        } else {
            TypeT init = contents.get(0).getTypeCheckType();
            if (init instanceof TypeTTau) {
                TypeTTau initTau = (TypeTTau) init;
                for (Expr e : contents) {
                    if (!initTau.equals(e.getTypeCheckType())) {
                        //TODO: throw error
                    }
                }
                // all taus are equal
                node.setTypeCheckType(new TypeTTauArray(initTau));
            } else {
                // TODO: throw error, init is not tau
            }
        }
    }

    @Override
    public void visit(UnopExpr node) {
        Expr e = node.getExpr();
        TypeT et = e.getTypeCheckType();
        TypeTTauInt i = new TypeTTauInt();
        TypeTTauBool b = new TypeTTauBool();
        switch (node.getOp()) {
            case NOT:
                if (et.equals(b)){
                    node.setTypeCheckType(b);
                } else {
                    //TODO: throw error with position
                } break;
            case UMINUS:
                if (et.equals(i)){
                    node.setTypeCheckType(i);
                } else {
                    //TODO: throw error with position
                } break;
        }
    }

    @Override
    public void visit(IndexAssignable node) {

    }

    @Override
    public void visit(UnderscoreAssignable node) {

    }

    @Override
    public void visit(IdAssignable node) {

    }

    @Override
    public void visit(ReturnStmt node) {

    }

    @Override
    public void visit(AssignStmt node) {

    }

    @Override
    public void visit(DeclStmt node) {

    }

    @Override
    public void visit(DeclAssignStmt node) {

    }

    @Override
    public void visit(ProcedureCallStmt node) {

    }

    @Override
    public void visit(IfStmt node) {

    }

    @Override
    public void visit(IfElseStmt node) {

    }

    @Override
    public void visit(WhileStmt node) {

    }

    @Override
    public void visit(BlockStmt node) {

    }

    @Override
    public void visit(ProgramFile node) {

    }

    @Override
    public void visit(InterfaceFile node) {

    }

    @Override
    public void visit(FuncDefn node) {

    }

    @Override
    public void visit(FuncDecl node) {

    }

    @Override
    public void visit(UseInterface node) {

    }
}