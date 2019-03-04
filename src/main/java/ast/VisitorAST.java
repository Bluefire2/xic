package ast;

interface VisitorAST<T> {
    T visit(ExprBinop node);
    T visit(ExprBoolLiteral node);
    T visit(ExprFunctionCall node);
    T visit(ExprId node);
    T visit(ExprIndex node);
    T visit(ExprIntLiteral node);
    T visit(ExprLength node);
    T visit(ExprArrayLiteral node);
    T visit(ExprUnop node);

    T visit(AssignableIndex node);
    T visit(AssignableUnderscore node);
    T visit(AssignableExpr node);

    T visit(StmtReturn node);
    T visit(StmtAssign node);
    T visit(StmtDecl node);
    T visit(StmtDeclAssign node);
    T visit(StmtProcedureCall node);
    T visit(StmtIf node);
    T visit(StmtIfElse node);
    T visit(StmtWhile node);
    T visit(StmtBlock node);

    T visit(FileProgram node);
    T visit(FileInterface node);
    T visit(FuncDefn node);
    T visit(FuncDecl node);
    T visit(UseInterface node);
}
