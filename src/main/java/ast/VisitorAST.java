package ast;

interface VisitorAST {
    void visit(ExprBinop node) throws SemanticErrorException;
    void visit(ExprBoolLiteral node) throws SemanticErrorException;
    void visit(ExprFunctionCall node) throws SemanticErrorException;
    void visit(ExprId node) throws SemanticErrorException;
    void visit(ExprIndex node) throws SemanticErrorException;
    void visit(ExprIntLiteral node) throws SemanticErrorException;
    void visit(ExprLength node) throws SemanticErrorException;
    void visit(ExprArrayLiteral node) throws SemanticErrorException;
    void visit(ExprUnop node) throws SemanticErrorException;

    void visit(AssignableIndex node) throws SemanticErrorException;
    void visit(AssignableUnderscore node) throws SemanticErrorException;
    void visit(AssignableId node) throws SemanticErrorException;

    void visit(StmtReturn node) throws SemanticErrorException;
    void visit(StmtAssign node) throws SemanticErrorException;
    void visit(StmtDecl node) throws SemanticErrorException;
    void visit(StmtDeclAssign node) throws SemanticErrorException;
    void visit(StmtProcedureCall node) throws SemanticErrorException;
    void visit(StmtIf node) throws SemanticErrorException;
    void visit(StmtIfElse node) throws SemanticErrorException;
    void visit(StmtWhile node) throws SemanticErrorException;
    void visit(StmtBlock node) throws SemanticErrorException;

    void visit(FileProgram node) throws SemanticErrorException;
    void visit(FileInterface node) throws SemanticErrorException;
    void visit(FuncDefn node) throws SemanticErrorException;
    void visit(FuncDecl node) throws SemanticErrorException;
    void visit(UseInterface node) throws SemanticErrorException;
}
