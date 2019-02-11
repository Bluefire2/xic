package xi_parser;

import java.util.*;

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
    UnderscoreExpr
}

enum StmtType {
    ProcedureReturnStmt,
    FunctionReturnStmt,
    AssignStmt,
    MultiAssignStmt,
    DeclStmt,
    MultiDeclStmt,
    DeclAssignStmt,
    MultiDeclAssignStmt,
    ProcedureCallStmt,
    IfStmt,
    IfElseStmt,
    WhileStmt,
    BlockStmt
}

enum TypeType {
    ListType,
    TupleType,
    Tvar
}

enum Unop {
    NOT,
    UMINUS
}

enum Binop {
    EQ, //=
    PLUS,
    MINUS,
    NOT,
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

class Pair<A, B> {
    private A first;
    private B second;

    Pair(A first, B second) {
        this.first = first;
        this.second = second;
    }

    public A fst() {
        return first;
    }

    public B snd() {
        return second;
    }

    public String toString() {
        return "(" + first.toString() + "," + second.toString() + ")";
    }
}

abstract class Type {
    public TypeType t_type;

    public TypeType getT_type() {
        return this.t_type;
    }
}

class Tvar extends Type {
    private String name;

    Tvar(String name) {
        this.name = name;
        this.t_type = TypeType.Tvar;
    }

    public String toString() {
        return name;
    }

    public String getName() {
        return name;
    }
}

class ListType extends Type {
    private Type contentsType;
    private Expr length;

    ListType(Type type) {
        this.contentsType = type;
        this.t_type = TypeType.ListType;
    }

    ListType(Type type, Expr length) {
        this.contentsType = type;
        this.length = length;
        this.t_type = TypeType.ListType;
    }

    public String toString() {
        return contentsType.toString() + " list";
    }

    public Type getContentsType() {
        return contentsType;
    }

    public Expr getLength() {
        return length;
    }
}

class TupleType extends Type {
    private List<Type> contentsTypes;

    TupleType(List<Type> types) {
        this.contentsTypes = types;
        this.t_type = TypeType.TupleType;
    }

    public String toString() {
        List<String> typeNames = new ArrayList<>();
        Arrays.asList(contentsTypes).forEach(elt -> typeNames.add(elt.toString()));
        return String.join(" * ", typeNames);
    }

    public List<Type> getContentsTypes() {
        return contentsTypes;
    }
}


class Expr {
    public ExprType e_type;

    public ExprType getE_type() {
        return e_type;
    }
}

class BinopExpr extends Expr {
    private Binop op;
    private Expr left;
    private Expr right;

    BinopExpr(Binop op, Expr left, Expr right) {
        this.op = op;
        this.left = left;
        this.right = right;
        this.e_type = ExprType.BinopExpr;
    }

    public Binop getOp() {
        return op;
    }

    public Expr getLeft() {
        return left;
    }

    public Expr getRight() {
        return right;
    }
}

class UnopExpr extends Expr {
    private Unop op;
    private Expr expr;

    UnopExpr(Unop op, Expr expr) {
        this.op = op;
        this.expr = expr;
        this.e_type = ExprType.UnopExpr;
    }

    public Unop getOp() {
        return op;
    }

    public Expr getExpr() {
        return expr;
    }
}

class IdExpr extends Expr {
    private String name;

    IdExpr(String name) {
        this.name = name;
        this.e_type = ExprType.IdExpr;
    }

    public String getName() {
        return name;
    }
}

class IntLiteralExpr extends Expr {
    private Long value;

    IntLiteralExpr(long val) {
        this.value = val;
        this.e_type = ExprType.IntLiteralExpr;
    }

    IntLiteralExpr(String val) {
        this.value = Long.parseLong(val);
        this.e_type = ExprType.IntLiteralExpr;
    }

    public Long getValue() {
        return value;
    }
}

class BoolLiteralExpr extends Expr {
    private Boolean value;

    BoolLiteralExpr(String val) {
        this.value = Boolean.parseBoolean(val);
        this.e_type = ExprType.BoolLiteralExpr;
    }

    public Boolean getValue() {
        return value;
    }
}

class ListLiteralExpr extends Expr {
    private List<Expr> contents;

    ListLiteralExpr(List<Expr> contents) {
        this.contents = contents;
        this.e_type = ExprType.ListLiteralExpr;
    }

    ListLiteralExpr(String value) {
        char[] chars = value.toCharArray();
        this.contents = new ArrayList<>();
        for (int i = 0; i < chars.length; i++) {
            contents.add(new IntLiteralExpr(chars[i]));
        }
    }

    public List<Expr> getContents() {
        return contents;
    }

    public int getLength() {
        return contents.size();
    }
}

class IndexExpr extends Expr {
    private Expr list;
    private Expr index;

    IndexExpr(Expr list, Expr index) {
        this.list = list;
        this.index = index;
        this.e_type = ExprType.IndexExpr;
    }

    public Expr getList() {
        return list;
    }

    public Expr getIndex() {
        return index;
    }
}

class FunctionCallExpr extends Expr {
    private String name;
    private List<Expr> args;

    FunctionCallExpr(String name, List<Expr> args) {
        this.name = name;
        this.args = args;
        this.e_type = ExprType.FunctionCallExpr;
    }

    public String getName() {
        return name;
    }

    public List<Expr> getArgs() {
        return args;
    }
}

class LengthExpr extends Expr {
    private Expr list;

    LengthExpr(Expr list) {
        this.list = list;
        this.e_type = ExprType.LengthExpr;
    }

    public Expr getList() {
        return list;
    }
}

class UnderscoreExpr extends Expr {
    UnderscoreExpr() {
        this.e_type = ExprType.UnderscoreExpr;
    }
}

class Stmt {
    public StmtType s_type;

    public StmtType getS_type() {
        return s_type;
    }

}

class FunctionReturnStmt extends Stmt {
    private Expr returnVal;

    FunctionReturnStmt(Expr returnVal) {
        this.returnVal = returnVal;
        this.s_type = StmtType.FunctionReturnStmt;
    }

    public Expr getReturnVal() {
        return returnVal;
    }
}

class ProcedureReturnStmt extends Stmt {
    ProcedureReturnStmt() {
        this.s_type = StmtType.ProcedureReturnStmt;
    }
}

class IfStmt extends Stmt {
    private Expr guard;
    private Stmt thenStmt;

    IfStmt(Expr guard, Stmt thenStmt) {
        this.guard = guard;
        this.thenStmt = thenStmt;
        this.s_type = StmtType.IfStmt;
    }
}

class IfElseStmt extends Stmt {
    private Expr guard;
    private Stmt thenStmt;
    private Stmt elseStmt;

    IfElseStmt(Expr guard, Stmt thenStmt, Stmt elseStmt) {
        this.guard = guard;
        this.thenStmt = thenStmt;
        this.elseStmt = elseStmt;
        this.s_type = StmtType.IfElseStmt;
    }
}

class WhileStmt extends Stmt {
    private Expr guard;
    private Stmt doStmt;

    WhileStmt(Expr guard, Stmt doStmt) {
        this.guard = guard;
        this.doStmt = doStmt;
        this.s_type = StmtType.WhileStmt;
    }

    public Expr getGuard() {
        return guard;
    }

    public Stmt getDoStmt() {
        return doStmt;
    }
}

class ProcedureCallStmt extends Stmt {
    private String name;
    private List<Expr> args;

    public ProcedureCallStmt(String name, List<Expr> args) {
        this.name = name;
        this.args = args;
        this.s_type = StmtType.ProcedureCallStmt;
    }

    public String getName() {
        return name;
    }

    public List<Expr> getArgs() {
        return args;
    }
}

class AssignStmt extends Stmt {
    private List<Expr> left;
    private List<Expr> right;

    public AssignStmt(List<Expr> left, List<Expr> right) {
        this.left = left;
        this.right = right;
        this.s_type = StmtType.MultiAssignStmt;
    }

    public List<Expr> getLeft() {
        return left;
    }

    public List<Expr> getRight() {
        return right;
    }
}

class DeclStmt extends Stmt {
    private List<Pair<String, Type>> decls;

    public DeclStmt(List<Pair<String, Type>> decls) {
        this.decls = decls;
        this.s_type = StmtType.MultiDeclStmt;
    }

    public List<Pair<String, Type>> getDecls() {
        return decls;
    }
}

class DeclAssignStmt extends Stmt {
    private List<Pair<String, Type>> decls;
    private List<Expr> right;

    public DeclAssignStmt(List<Pair<String, Type>> decls, List<Expr> right) {
        this.decls = decls;
        this.right = right;
        this.s_type = StmtType.MultiDeclAssignStmt;
    }

    public List<Pair<String, Type>> getDecls() {
        return decls;
    }

    public List<Expr> getRight() {
        return right;
    }
}

class BlockStmt extends Stmt {
    private List<Stmt> statements;

    public BlockStmt(List<Stmt> statements) {
        this.statements = statements;
        this.s_type = StmtType.BlockStmt;
    }

    public BlockStmt() {
        this(new ArrayList<>());
    }

    public List<Stmt> getStatments() {
        return statements;
    }

    public boolean isEmpty() {
        return statements.size() == 0;
    }

    public Stmt getLastStatement() {
        return statements.get(statements.size() - 1);
    }
}

//definitions are for program files
abstract class Defn {
    public boolean isProcedure;
}

abstract class FPDefn extends Defn {
    String name;
    List<Pair<String, Type>> params;
    Stmt body;

    public String getName() {
        return name;
    }

    public List<Pair<String, Type>> getParams() {
        return params;
    }

    public Stmt getBody() {
        return body;
    }

    abstract boolean isProcedure();
}

class FunctionDefn extends FPDefn {
    private Type output;

    public FunctionDefn(String name, List<Pair<String, Type>> params, Type output, Stmt body) {
        this.name = name;
        this.params = params;
        this.output = output;
        this.body = body;
    }

    public Type getOutput() {
        return output;
    }

    @Override
    public boolean isProcedure() {
        return false;
    }
}

class ProcedureDefn extends FPDefn {
    public ProcedureDefn(String name, List<Pair<String, Type>> params, Stmt body) {
        this.name = name;
        this.params = params;
        this.body = body;
    }

    @Override
    public boolean isProcedure() {
        return true;
    }

}

class UseInterface {
    private String name;

    public UseInterface(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}

//declarations are for interfaces
abstract class Decl {
    abstract boolean isProcedure();
}

class FunctionDecl extends Decl {
    private String name;
    private List<Pair<String, Type>> params;
    private Type output;

    public FunctionDecl(String name, List<Pair<String, Type>> params, Type output) {
        this.name = name;
        this.params = params;
        this.output = output;
    }

    public String getName() {
        return name;
    }

    public List<Pair<String, Type>> getParams() {
        return params;
    }

    public Type getOutput() {
        return output;
    }

    public boolean isProcedure() {
        return false;
    }
}

class ProcedureDecl extends Decl {
    private String name;
    private List<Pair<String, Type>> params;

    public ProcedureDecl(String name, List<Pair<String, Type>> params) {
        this.name = name;
        this.params = params;
    }

    public String getName() {
        return name;
    }

    public List<Pair<String, Type>> getParams() {
        return params;
    }

    public boolean isProcedure() {
        return true;
    }
}

//top level "nodes"
abstract class SourceFile {
    abstract boolean isInterface();
}

class InterfaceFile extends SourceFile {
    private ArrayList<Decl> declarations;

    public InterfaceFile(List<Decl> declarations) {
        this.declarations = new ArrayList<>(declarations);
    }

    public List<Decl> getDeclarations() {
        return declarations;
    }

    public void addDecl(Decl decl) {
        declarations.add(decl);
    }

    public boolean isInterface() {
        return true;
    }
}

class ProgramFile extends SourceFile {
    private ArrayList<UseInterface> imports;
    private ArrayList<Defn> definitions;

    public ProgramFile(List<UseInterface> imports, List<Defn> definitions) {
        this.imports = new ArrayList<>(imports);
        this.definitions = new ArrayList<>(definitions);
    }

    public List<UseInterface> getImports() {
        return imports;
    }

    public List<Defn> getDefinitions() {
        return definitions;
    }

    public void addDefn(Defn defn) {
        definitions.add(defn);
    }

    public void addImport(UseInterface use) {
        imports.add(use);
    }

    public boolean isInterface() {
        return false;
    }
}
