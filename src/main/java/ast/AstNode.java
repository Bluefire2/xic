package ast;

enum ExprType {
    BinopExpr,
    BoolLiteralExpr,
    FunctionCallExpr,
    IdExpr,
    IndexExpr,
    IntLiteralExpr,
    LengthExpr,
    ListLiteralExpr,
    UnopExpr,
}

enum StmtType {
    ProcedureReturnStmt,
    FunctionReturnStmt,
    AssignStmt,
    DeclStmt,
    DeclAssignStmt,
    ProcedureCallStmt,
    IfStmt,
    IfElseStmt,
    WhileStmt,
    BlockStmt
}

enum TypeType {
    ListType,
    AnyType,
    Tvar
}

enum Unop {
    NOT,
    UMINUS
}

enum Binop {
    PLUS,
    MINUS,
    MULT,
    HI_MULT,// *>>
    DIV,
    MOD,
    EQEQ,// ==
    NEQ,
    GT,
    LT,
    GTEQ,
    LTEQ,
    AND,
    OR
}


