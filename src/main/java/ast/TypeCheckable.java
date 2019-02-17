package ast;

import symboltable.*;
import java.util.List;
import java.util.ArrayList;

enum StmtFallthrough {
    Unit, // different from TypeType.UnitType
    Void,
}

interface ASTVisitor {
    void visit(BinopExpr node);
    void visit(BoolLiteralExpr node);
    void visit(FunctionCallExpr node);
    void visit(IdExpr node);
    void visit(IndexExpr node);
    void visit(IntLiteralExpr node);
    void visit(LengthExpr node);
    void visit(ListLiteralExpr node);
    void visit(UnopExpr node);

    void visit(IndexAssignable node);
    void visit(UnderscoreAssignable node);
    void visit(IdAssignable node);

    void visit(ReturnStmt node);
    void visit(AssignStmt node);
    void visit(DeclStmt node);
    void visit(DeclAssignStmt node);
    void visit(ProcedureCallStmt node);
    void visit(IfStmt node);
    void visit(IfElseStmt node);
    void visit(WhileStmt node);
    void visit(BlockStmt node);

    void visit(ProgramFile node);
    void visit(InterfaceFile node);
    void visit(FuncDefn node);
    void visit(FuncDecl node);
    void visit(UseInterface node);
}

interface TypeCheckable {
    void accept(TypeCheckVisitor visitor);
}

class TypeCheckVisitor implements ASTVisitor {
    public SymbolTable context;

    TypeCheckVisitor(SymbolTable st){
        this.context = st;
    }

    @Override
    public void visit(BinopExpr node) {
        Expr left = node.getLeft();
        Expr right = node.getRight();
        MetaType lmt = left.typeCheckType;
        MetaType rmt = right.typeCheckType;
        IntType i = new IntType();
        BoolType b = new BoolType();
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
        node.setTypeCheckType(new BoolType());
    }

    @Override
    public void visit(FunctionCallExpr node) {
        //TODO: I think we need a meta-type otherwise cannot set type to a list
        String name = node.getName();
        try {
            CtxType t = context.lookup(name);
            if (t instanceof FunCtxType) {
                MetaType inTypes = ((FunCtxType) t).getInputs();
                MetaType outTypes = ((FunCtxType) t).getOutputs();
                List<Expr> args = node.getArgs();
                //TODO INCOMPLETE
                } else {
                    //TODO: throw error
                }
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
            CtxType t = context.lookup(name);
            if (t instanceof VarCtxType) {
                node.setTypeCheckType(((VarCtxType) t).getType());
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
        MetaType lt = lst.getTypeCheckType();
        MetaType it = idx.getTypeCheckType();
        if (lt instanceof ListType) {
            if (it instanceof IntType) {
                node.setTypeCheckType(((ListType) lt).getContentsType());
            } else {
                //TODO: throw error
            }
        } else {
            //TODO: throw error
        }
    }

    @Override
    public void visit(IntLiteralExpr node) {
        node.setTypeCheckType(new IntType());
    }

    @Override
    public void visit(LengthExpr node) {
        if (node.getTypeCheckType() instanceof ListType){
            node.setTypeCheckType(new IntType());
        } else {
            //TODO: throw error
        }
    }

    @Override
    public void visit(ListLiteralExpr node) { //TODO: handle lengths somehow
        List<Expr> contents = node.getContents();
        int length = node.getLength();
        if (length == 0) {
            node.setTypeCheckType(new ListType(new UnitType()));
        } else {
            MetaType init = contents.get(0).getTypeCheckType();
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
        MetaType et = e.getTypeCheckType();
        IntType i = new IntType();
        BoolType b = new BoolType();
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