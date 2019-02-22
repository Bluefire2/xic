package ast;

interface VisitorAST {
    void visit(ExprBinop node) throws ASTException;
    void visit(ExprBoolLiteral node) throws ASTException;
    void visit(ExprFunctionCall node) throws ASTException;
    void visit(ExprId node) throws ASTException;
    void visit(ExprIndex node) throws ASTException;
    void visit(ExprIntLiteral node) throws ASTException;
    void visit(ExprLength node) throws ASTException;
    void visit(ExprArrayLiteral node) throws ASTException;
    void visit(ExprUnop node) throws ASTException;

    void visit(AssignableIndex node) throws ASTException;
    void visit(AssignableUnderscore node) throws ASTException;
    void visit(AssignableId node) throws ASTException;

    void visit(StmtReturn node) throws ASTException;
    void visit(StmtAssign node) throws ASTException;
    void visit(StmtDecl node) throws ASTException;
    void visit(StmtDeclAssign node) throws ASTException;
    void visit(StmtProcedureCall node) throws ASTException;
    void visit(StmtIf node) throws ASTException;
    void visit(StmtIfElse node) throws ASTException;
    void visit(StmtWhile node) throws ASTException;
    void visit(StmtBlock node) throws ASTException;

    void visit(FileProgram node) throws ASTException;
    void visit(FileInterface node) throws ASTException;
    void visit(FuncDefn node) throws ASTException;
    void visit(FuncDecl node) throws ASTException;
    void visit(UseInterface node) throws ASTException;
}
