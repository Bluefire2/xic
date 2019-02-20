package ast;

interface VisitorAST {
    void visit(BinopExpr node) throws SemanticErrorException;
    void visit(BoolLiteralExpr node) throws SemanticErrorException;
    void visit(FunctionCallExpr node) throws SemanticErrorException;
    void visit(IdExpr node) throws SemanticErrorException;
    void visit(IndexExpr node) throws SemanticErrorException;
    void visit(IntLiteralExpr node) throws SemanticErrorException;
    void visit(LengthExpr node) throws SemanticErrorException;
    void visit(ArrayLiteralExpr node) throws SemanticErrorException;
    void visit(UnopExpr node) throws SemanticErrorException;

    void visit(IndexAssignable node) throws SemanticErrorException;
    void visit(UnderscoreAssignable node) throws SemanticErrorException;
    void visit(IdAssignable node) throws SemanticErrorException;

    void visit(ReturnStmt node) throws SemanticErrorException;
    void visit(AssignStmt node) throws SemanticErrorException;
    void visit(DeclStmt node) throws SemanticErrorException;
    void visit(DeclAssignStmt node) throws SemanticErrorException;
    void visit(ProcedureCallStmt node) throws SemanticErrorException;
    void visit(IfStmt node) throws SemanticErrorException;
    void visit(IfElseStmt node) throws SemanticErrorException;
    void visit(WhileStmt node) throws SemanticErrorException;
    void visit(BlockStmt node) throws SemanticErrorException;

    void visit(ProgramFile node) throws SemanticErrorException;
    void visit(InterfaceFile node) throws SemanticErrorException;
    void visit(FuncDefn node) throws SemanticErrorException;
    void visit(FuncDecl node) throws SemanticErrorException;
    void visit(UseInterface node) throws SemanticErrorException;
}
