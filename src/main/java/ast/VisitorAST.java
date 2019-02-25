package ast;

interface VisitorAST {
    void visit(ExprBinop node);
    void visit(ExprBoolLiteral node);
    void visit(ExprFunctionCall node);
    void visit(ExprId node);
    void visit(ExprIndex node);
    void visit(ExprIntLiteral node);
    void visit(ExprLength node);
    void visit(ExprArrayLiteral node);
    void visit(ExprUnop node);

    void visit(AssignableIndex node);
    void visit(AssignableUnderscore node);
    void visit(AssignableExpr node);

    void visit(StmtReturn node);
    void visit(StmtAssign node);
    void visit(StmtDecl node);
    void visit(StmtDeclAssign node);
    void visit(StmtProcedureCall node);
    void visit(StmtIf node);
    void visit(StmtIfElse node);
    void visit(StmtWhile node);
    void visit(StmtBlock node);

    void visit(FileProgram node);
    void visit(FileInterface node);
    void visit(FuncDefn node);
    void visit(FuncDecl node);
    void visit(UseInterface node);
}
