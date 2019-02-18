package ast;

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
