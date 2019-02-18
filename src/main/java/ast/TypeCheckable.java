package ast;

import symboltable.*;
import java.util.List;

interface TypeCheckable {
    void accept(TypeCheckVisitor visitor);
}

class TypeCheckVisitor implements ASTVisitor {
    public SymbolTable symTable;

    TypeCheckVisitor(SymbolTable symTable){
        this.symTable = symTable;
    }

    @Override
    public void visit(BinopExpr node) {
        Expr left = node.getLeft();
        Expr right = node.getRight();
        TypeT lmt = left.typeCheckType;
        TypeT rmt = right.typeCheckType;
        TypeTTauInt i = new TypeTTauInt();
        TypeTTauBool b = new TypeTTauBool();
        boolean bothInt = i.equals(lmt) && i.equals(rmt);
        boolean bothBool = b.equals(lmt) && b.equals(rmt);
        switch (node.getOp()) {
            case PLUS:
            case MINUS:
            case MULT:
            case HI_MULT:
            case DIV:
            case MOD:
                if (bothInt){
                    node.setTypeCheckType(i);
                } else {
                    //TODO: throw error with position
                } break;
            case EQEQ:
            case NEQ:
                if (bothInt || bothBool){
                    node.setTypeCheckType(b);
                } else {
                    //TODO: throw error with position
                } break;
            case GT:
            case LT:
            case GTEQ:
            case LTEQ:
                if (bothInt){
                    node.setTypeCheckType(b);
                } else {
                    //TODO: throw error with position
                } break;
            case AND:
            case OR:
                if (bothBool){
                    node.setTypeCheckType(b);
                } else {
                    //TODO: throw error with position
                } break;
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
                List<Expr> args = node.getArgs();
                //TODO INCOMPLETE
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
        Expr lst = node.getList();
        Expr idx = node.getIndex();
        TypeT lt = lst.getTypeCheckType();
        TypeT it = idx.getTypeCheckType();
        if (lt instanceof TypeTTauArray) {
            if (it instanceof TypeTTauInt) {
                node.setTypeCheckType(((TypeTTauArray) lt).getTypeTTau());
            } else {
                //TODO: throw error
            }
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
    public void visit(ListLiteralExpr node) { //TODO: handle lengths somehow
        List<Expr> contents = node.getContents();
        int length = node.getLength();
        if (length == 0) {
            node.setTypeCheckType(new TypeTTauArray(new TypeTUnit()));
        } else {
            TypeT init = contents.get(0).getTypeCheckType();
            for (Expr e: contents) {
                if (!(e.getTypeCheckType().equals(init))){
                    //TODO: throw error
                }
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