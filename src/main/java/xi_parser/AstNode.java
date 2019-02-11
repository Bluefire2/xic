package xi_parser;

import polyglot.util.Pair;

import java.util.ArrayList;
import java.util.List;

import edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;

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

interface Printable {
    void prettyPrint(CodeWriterSExpPrinter w);
}

abstract class Type implements Printable {
    public TypeType t_type;

    public TypeType getT_type() {
        return this.t_type;
    }
}

class AnyType extends Type{
    AnyType() {
        this.t_type = TypeType.AnyType;
    }

    public void prettyPrint(CodeWriterSExpPrinter w) { }
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

    public void prettyPrint(CodeWriterSExpPrinter w) {
        w.printAtom(this.toString());
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

    public void prettyPrint(CodeWriterSExpPrinter w) {
        w.startList();
        w.printAtom("[]");
        contentsType.prettyPrint(w);
        if (length != null){
            length.prettyPrint(w);
        }
        w.endList();
    }
}


abstract class Expr implements Printable {
    ExprType e_type;

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

    public String opToString(){
        String opString = "";
        switch (op) {
            case PLUS: opString = "+"; break;
            case MINUS: opString = "-"; break;
            case MULT: opString = "*"; break;
            case HI_MULT: opString = "*>>"; break;
            case DIV: opString = "/"; break;
            case MOD: opString = "%"; break;
            case EQEQ: opString = "=="; break;
            case NEQ: opString = "!="; break;
            case GT: opString = ">"; break;
            case LT: opString = "<"; break;
            case GTEQ: opString = ">="; break;
            case LTEQ: opString = "<="; break;
            case AND: opString = "&"; break;
            case OR: opString = "|"; break;
        }
        return opString;
    }

    public void prettyPrint(CodeWriterSExpPrinter w) {
        w.startList();
        w.printAtom(this.opToString());
        left.prettyPrint(w);
        right.prettyPrint(w);
        w.endList();
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

    public String opToString(){
        String opString = "";
        switch (op) {
            case NOT: opString = "!"; break;
            case UMINUS: opString = "-"; break;
        }
        return opString;
    }

    public void prettyPrint(CodeWriterSExpPrinter w) {
        w.startList();
        w.printAtom(this.opToString());
        expr.prettyPrint(w);
        w.endList();
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

    public void prettyPrint(CodeWriterSExpPrinter w) {
        w.printAtom(name);
    }
}

class IntLiteralExpr extends Expr {
    private Long value;
    public boolean isChar;

    IntLiteralExpr(Long val) {
        this.value = val;
        this.e_type = ExprType.IntLiteralExpr;
        this.isChar = false;
    }

    IntLiteralExpr(Character val) {
        this.value = (long) Character.getNumericValue(val);
        this.e_type = ExprType.IntLiteralExpr;
        this.isChar = true;
    }

    public Long getValue() {
        return value;
    }

    public void prettyPrint(CodeWriterSExpPrinter w) {
        w.printAtom(value.toString());
    }
}

class BoolLiteralExpr extends Expr {
    private Boolean value;

    BoolLiteralExpr(Boolean val) {
        this.value = val;
        this.e_type = ExprType.BoolLiteralExpr;
    }

    public Boolean getValue() {
        return value;
    }

    public void prettyPrint(CodeWriterSExpPrinter w) {
        w.printAtom(value.toString());
    }
}

class ListLiteralExpr extends Expr {
    private List<Expr> contents;
    public boolean isString;

    ListLiteralExpr(List<Expr> contents) {
        this.contents = contents;
        this.e_type = ExprType.ListLiteralExpr;
        this.isString = false;
    }

    ListLiteralExpr(String value) {
        char[] chars = value.toCharArray();
        this.contents = new ArrayList<>();
        for (int i = 0; i < chars.length; i++) {
            contents.add(new IntLiteralExpr(chars[i]));
        }
        this.isString = true;
    }

    public List<Expr> getContents() {
        return contents;
    }

    public int getLength() {
        return contents.size();
    }

    public void prettyPrint(CodeWriterSExpPrinter w) {
        w.startList();
        contents.forEach((e) -> e.prettyPrint(w));
        w.endList();
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

    public void prettyPrint(CodeWriterSExpPrinter w) {
        w.startList();
        w.printAtom("[]");
        list.prettyPrint(w);
        index.prettyPrint(w);
        w.endList();
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

    public void prettyPrint(CodeWriterSExpPrinter w) {
        w.startList();
        w.printAtom(name);
        args.forEach((a) -> a.prettyPrint(w));
        w.endList();
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

    public void prettyPrint(CodeWriterSExpPrinter w) {
        w.startList();
        w.printAtom("length");
        list.prettyPrint(w);
        w.endList();
    }
}

class UnderscoreExpr extends Expr {
    UnderscoreExpr() {
        this.e_type = ExprType.UnderscoreExpr;
    }

    public void prettyPrint(CodeWriterSExpPrinter w) {
        w.printAtom("_");
    }
}

abstract class Stmt implements Printable {
    StmtType s_type;

    public StmtType getS_type() {
        return s_type;
    }

    public void printPair(Pair<String, Type> p, CodeWriterSExpPrinter w){
        if (p.part2().getT_type() != TypeType.AnyType){
            w.startList();
            w.printAtom(p.part1());
            p.part2().prettyPrint(w);
            w.endList();
        } else {
            w.printAtom("_");
        }
    }
}

class ReturnStmt extends Stmt {
    private List<Expr> returnVals;

    ReturnStmt(List<Expr> returnVals) {
        this.returnVals = returnVals;
        this.s_type = StmtType.FunctionReturnStmt;
    }

    ReturnStmt() {
        this.returnVals = new ArrayList<Expr>();
        this.s_type = StmtType.ProcedureReturnStmt;
    }

    public List<Expr> getReturnVals() {
        return returnVals;
    }

    public void prettyPrint(CodeWriterSExpPrinter w) {
        w.startList();
        w.printAtom("return");
        returnVals.forEach((v) -> v.prettyPrint(w));
        w.endList();
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

    public void prettyPrint(CodeWriterSExpPrinter w) {
        w.startUnifiedList();
        guard.prettyPrint(w);
        thenStmt.prettyPrint(w);
        w.endList();
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

    public void prettyPrint(CodeWriterSExpPrinter w) {
        w.startUnifiedList();
        guard.prettyPrint(w);
        thenStmt.prettyPrint(w);
        elseStmt.prettyPrint(w);
        w.endList();
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

    public void prettyPrint(CodeWriterSExpPrinter w) {
        w.startUnifiedList();
        guard.prettyPrint(w);
        doStmt.prettyPrint(w);
        w.endList();
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

    public void prettyPrint(CodeWriterSExpPrinter w) {
        w.startList();
        w.printAtom(name);
        args.forEach((a) -> a.prettyPrint(w));
        w.endList();
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

    public void prettyPrint(CodeWriterSExpPrinter w) {
        w.startList();
        w.printAtom("=");
        if (left.size() == 1){
            left.get(0).prettyPrint(w);
        } else {
            w.startList();
            left.forEach((e) -> e.prettyPrint(w));
            w.endList();
        }
        if (right.size() == 1){
            right.get(0).prettyPrint(w);
        } else {
            w.startList();
            right.forEach((e) -> e.prettyPrint(w));
            w.endList();
        }
        w.endList();
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

    public void prettyPrint(CodeWriterSExpPrinter w) {
        if (decls.size() == 1){
            this.printPair(decls.get(0),w);
        } else {
            w.startList();
            decls.forEach((d) -> this.printPair(d, w));
            w.endList();
        }
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

    public void prettyPrint(CodeWriterSExpPrinter w) {
        w.startList();
        w.printAtom("=");
        if (decls.size() == 1){
            this.printPair(decls.get(0),w);
        } else {
            w.startList();
            decls.forEach((d) -> this.printPair(d,w));
            w.endList();
        }
        w.endList();
        if (right.size() == 1){
            right.get(0).prettyPrint(w);
        } else {
            w.startList();
            right.forEach((e) -> e.prettyPrint(w));
            w.endList();
        }
        w.endList();
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

    public void prettyPrint(CodeWriterSExpPrinter w) {
        w.startUnifiedList();
        statements.forEach((s) -> s.prettyPrint(w));
        w.endList();
    }
}

//definitions are for program files
class Defn implements Printable {
    private String name;
    private List<Pair<String, Type>> params;
    private Stmt body;
    private List<Type> output;

    public Defn(String name, List<Pair<String, Type>> params, List<Type> output, Stmt body) {
        this.name = name;
        this.params = params;
        this.output = output;
        this.body = body;
    }

    public Defn(String name, List<Pair<String, Type>> params, Stmt body) {
        this.name = name;
        this.params = params;
        this.body = body;
        this.output = new ArrayList<Type>();
    }

    public String getName() {
        return name;
    }

    public List<Pair<String, Type>> getParams() {
        return params;
    }

    public Stmt getBody() {
        return body;
    }

    public List<Type> getOutput() {
        return output;
    }

    public boolean isProcedure() {
        return output.size()==0;
    }

    public void printPair(Pair<String, Type> p, CodeWriterSExpPrinter w){
        if (p.part2().getT_type() != TypeType.AnyType){
            w.startList();
            w.printAtom(p.part1());
            p.part2().prettyPrint(w);
            w.endList();
        } else {
            w.printAtom("_");
        }
    }

    public void prettyPrint(CodeWriterSExpPrinter w){
        w.startList();
        w.printAtom(name);
        w.startList();
        params.forEach((p) -> this.printPair(p, w));
        w.endList();
        w.startList();
        output.forEach((t) -> t.prettyPrint(w));
        w.endList();
        body.prettyPrint(w);
        w.endList();
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

    public void prettyPrint(CodeWriterSExpPrinter w) {
        w.startList();
        w.printAtom("use");
        w.printAtom(name);
        w.endList();
    }
}

//declarations are for interfaces
class Decl implements Printable {
    private String name;
    private List<Pair<String, Type>> params;
    private List<Type> output;

    public Decl(String name, List<Pair<String, Type>> params, List<Type> output) {
        this.name = name;
        this.params = params;
        this.output = output;
    }

    public Decl(String name, List<Pair<String, Type>> params) {
        this.name = name;
        this.params = params;
        this.output = new ArrayList<Type>();
    }

    public String getName() {
        return name;
    }

    public List<Pair<String, Type>> getParams() {
        return params;
    }

    public List<Type> getOutput() {
        return output;
    }

    public boolean isProcedure() {
        return output.size()==0;
    }

    public void printPair(Pair<String, Type> p, CodeWriterSExpPrinter w){
        if (p.part2().getT_type() != TypeType.AnyType){
            w.startList();
            w.printAtom(p.part1());
            p.part2().prettyPrint(w);
            w.endList();
        } else {
            w.printAtom("_");
        }
    }

    public void prettyPrint(CodeWriterSExpPrinter w){
        w.startList();
        w.printAtom(name);
        w.startList();
        params.forEach((p) -> this.printPair(p, w));
        w.endList();
        w.startList();
        output.forEach((t) -> t.prettyPrint(w));
        w.endList();
        w.endList();
    }
}

//top level "nodes"
abstract class SourceFile implements Printable {
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

    public void prettyPrint(CodeWriterSExpPrinter w) {
        w.startUnifiedList();
        w.startUnifiedList();
        declarations.forEach((i) -> i.prettyPrint(w));
        w.endList();
        w.endList();
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

    public void prettyPrint(CodeWriterSExpPrinter w) {
        w.startUnifiedList();
        w.startUnifiedList();
        imports.forEach((i) -> i.prettyPrint(w));
        w.endList();
        w.startUnifiedList();
        definitions.forEach((d) -> d.prettyPrint(w));
        w.endList();
        w.endList();
    }
}

