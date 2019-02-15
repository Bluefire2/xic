package xi_parser;

import edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;
import org.apache.commons.lang3.StringEscapeUtils;
import polyglot.util.Pair;

import java.util.ArrayList;
import java.util.List;

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

abstract class Node {
    int start_line;
    int start_col;
}

abstract class Type extends Node implements Printable {
    public TypeType t_type;

    public TypeType getT_type() {
        return this.t_type;
    }
}

class AnyType extends Type{
    AnyType(int start_line, int start_col) {
        this.t_type = TypeType.AnyType;
        this.start_line = start_line;
        this.start_col = start_col;
    }

    public void prettyPrint(CodeWriterSExpPrinter w) { }
}

class Tvar extends Type {
    private String name;

    Tvar(String name, int start_line, int start_col) {
        this.name = name;
        this.t_type = TypeType.Tvar;
        this.start_line = start_line;
        this.start_col = start_col;
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

    ListType(Type type, int start_line, int start_col) {
        this.contentsType = type;
        this.t_type = TypeType.ListType;
        this.start_line = start_line;
        this.start_col = start_col;
    }

    ListType(Type type, Expr length, int start_line, int start_col) {
        this.contentsType = type;
        this.length = length;
        this.t_type = TypeType.ListType;
        this.start_line = start_line;
        this.start_col = start_col;
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

    public void setContentsType(Type contentsType) {
        this.contentsType = contentsType;
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


abstract class Expr extends Node implements Printable {
    ExprType e_type;

    public ExprType getE_type() {
        return e_type;
    }
}

class BinopExpr extends Expr {
    private Binop op;
    private Expr left;
    private Expr right;

    BinopExpr(Binop op, Expr left, Expr right, int start_line, int start_col) {
        this.op = op;
        this.left = left;
        this.right = right;
        this.e_type = ExprType.BinopExpr;
        this.start_line = start_line;
        this.start_col = start_col;
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

    UnopExpr(Unop op, Expr expr, int start_line, int start_col) {
        this.op = op;
        this.expr = expr;
        this.e_type = ExprType.UnopExpr;
        this.start_line = start_line;
        this.start_col = start_col;
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

    IdExpr(String name, int start_line, int start_col) {
        this.name = name;
        this.e_type = ExprType.IdExpr;
        this.start_line = start_line;
        this.start_col = start_col;
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
    private Character raw;
    public boolean isChar;

    IntLiteralExpr(Long val, int start_line, int start_col) {
        this.value = val;
        this.e_type = ExprType.IntLiteralExpr;
        this.isChar = false;
        this.start_line = start_line;
        this.start_col = start_col;
    }

    IntLiteralExpr(Character val, int start_line, int start_col) {
        this.value = (long) Character.getNumericValue(val);
        this.e_type = ExprType.IntLiteralExpr;
        this.isChar = true;
        this.raw = val;
        this.start_line = start_line;
        this.start_col = start_col;
    }

    public Long getValue() {
        return value;
    }

    public void prettyPrint(CodeWriterSExpPrinter w) {
        if (this.isChar) {
            w.printAtom("\'"+StringEscapeUtils.escapeJava(raw.toString())+"\'");
        } else {
            w.printAtom(value.toString());
        }
    }
}

class BoolLiteralExpr extends Expr {
    private Boolean value;

    BoolLiteralExpr(Boolean val, int start_line, int start_col) {
        this.value = val;
        this.e_type = ExprType.BoolLiteralExpr;
        this.start_line = start_line;
        this.start_col = start_col;
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
    private String raw;

    ListLiteralExpr(List<Expr> contents, int start_line, int start_col) {
        this.contents = contents;
        this.e_type = ExprType.ListLiteralExpr;
        this.isString = false;
        this.start_line = start_line;
        this.start_col = start_col;
    }

    ListLiteralExpr(String value, int start_line, int start_col) {
        char[] chars = value.toCharArray();
        this.contents = new ArrayList<>();
        for (int i = 0; i < chars.length; i++) {
            contents.add(new IntLiteralExpr(chars[i], start_line, start_col));
        }
        this.isString = true;
        this.raw = value;
        this.start_line = start_line;
        this.start_col = start_col;
    }

    public List<Expr> getContents() {
        return contents;
    }

    public int getLength() {
        return contents.size();
    }

    public void prettyPrint(CodeWriterSExpPrinter w) {
        if (this.isString) {
            w.printAtom("\""+ StringEscapeUtils.escapeJava(raw) +"\"");
        } else {
            w.startList();
            contents.forEach((e) -> e.prettyPrint(w));
            w.endList();
        }
    }
}

class IndexExpr extends Expr {
    private Expr list;
    private Expr index;

    IndexExpr(Expr list, Expr index, int start_line, int start_col) {
        this.list = list;
        this.index = index;
        this.e_type = ExprType.IndexExpr;
        this.start_line = start_line;
        this.start_col = start_col;
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

    FunctionCallExpr(String name, List<Expr> args, int start_line, int start_col) {
        this.name = name;
        this.args = args;
        this.e_type = ExprType.FunctionCallExpr;
        this.start_line = start_line;
        this.start_col = start_col;
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

    LengthExpr(Expr list, int start_line, int start_col) {
        this.list = list;
        this.e_type = ExprType.LengthExpr;
        this.start_line = start_line;
        this.start_col = start_col;
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

abstract class Stmt extends Node implements Printable {
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

    ReturnStmt(List<Expr> returnVals, int start_line, int start_col) {
        this.returnVals = returnVals;
        this.s_type = StmtType.FunctionReturnStmt;
        this.start_line = start_line;
        this.start_col = start_col;
    }

    ReturnStmt(int start_line, int start_col) {
        this.returnVals = new ArrayList<Expr>();
        this.s_type = StmtType.ProcedureReturnStmt;
        this.start_line = start_line;
        this.start_col = start_col;
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

    IfStmt(Expr guard, Stmt thenStmt, int start_line, int start_col) {
        this.guard = guard;
        this.thenStmt = thenStmt;
        this.s_type = StmtType.IfStmt;
        this.start_line = start_line;
        this.start_col = start_col;
    }

    public void prettyPrint(CodeWriterSExpPrinter w) {
        w.startUnifiedList();
        w.printAtom("if");
        guard.prettyPrint(w);
        thenStmt.prettyPrint(w);
        w.endList();
    }
}

class IfElseStmt extends Stmt {
    private Expr guard;
    private Stmt thenStmt;
    private Stmt elseStmt;

    IfElseStmt(Expr guard, Stmt thenStmt, Stmt elseStmt, int start_line, int start_col) {
        this.guard = guard;
        this.thenStmt = thenStmt;
        this.elseStmt = elseStmt;
        this.s_type = StmtType.IfElseStmt;
        this.start_line = start_line;
        this.start_col = start_col;
    }

    public void prettyPrint(CodeWriterSExpPrinter w) {
        w.startUnifiedList();
        w.printAtom("if");
        guard.prettyPrint(w);
        thenStmt.prettyPrint(w);
        elseStmt.prettyPrint(w);
        w.endList();
    }
}

class WhileStmt extends Stmt {
    private Expr guard;
    private Stmt doStmt;

    WhileStmt(Expr guard, Stmt doStmt, int start_line, int start_col) {
        this.guard = guard;
        this.doStmt = doStmt;
        this.s_type = StmtType.WhileStmt;
        this.start_line = start_line;
        this.start_col = start_col;
    }

    public Expr getGuard() {
        return guard;
    }

    public Stmt getDoStmt() {
        return doStmt;
    }

    public void prettyPrint(CodeWriterSExpPrinter w) {
        w.startUnifiedList();
        w.printAtom("while");
        guard.prettyPrint(w);
        doStmt.prettyPrint(w);
        w.endList();
    }
}

class ProcedureCallStmt extends Stmt {
    private String name;
    private List<Expr> args;

    public ProcedureCallStmt(String name, List<Expr> args, int start_line, int start_col) {
        this.name = name;
        this.args = args;
        this.s_type = StmtType.ProcedureCallStmt;
        this.start_line = start_line;
        this.start_col = start_col;
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

abstract class Assignable extends Node implements Printable {

}

class UnderscoreAssignable extends Assignable {
    @Override
    public void prettyPrint(CodeWriterSExpPrinter w) {
        w.printAtom("_");
    }
}

class IdAssignable extends Assignable {
    private Expr id;

    IdAssignable(Expr id, int start_line, int start_col) {
        this.id = id;
        this.start_line = start_line;
        this.start_col = start_col;
    }

    public Expr getId() {
        return id;
    }

    @Override
    public void prettyPrint(CodeWriterSExpPrinter w) {
        id.prettyPrint(w);
    }
}

class IndexAssignable extends Assignable {
    private Expr index; // has to be an ID!

    IndexAssignable(Expr index, int start_line, int start_col) {
        this.index = index;
        this.start_line = start_line;
        this.start_col = start_col;
    }

    public Expr getIndex() {
        return index;
    }

    @Override
    public void prettyPrint(CodeWriterSExpPrinter w) {
        index.prettyPrint(w);
    }
}

class AssignStmt extends Stmt {
    private List<Assignable> left;
    private List<Expr> right;

    public AssignStmt(List<Assignable> left, List<Expr> right, int start_line, int start_col) {
        this.left = left;
        this.right = right;
        this.s_type = StmtType.AssignStmt;
        this.start_line = start_line;
        this.start_col = start_col;
    }

    public List<Assignable> getLeft() {
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

    public DeclStmt(List<Pair<String, Type>> decls, int start_line, int start_col) {
        this.decls = decls;
        this.s_type = StmtType.DeclStmt;
        this.start_line = start_line;
        this.start_col = start_col;
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

    public DeclAssignStmt(List<Pair<String, Type>> decls, List<Expr> right, int start_line, int start_col) {
        this.decls = decls;
        this.right = right;
        this.s_type = StmtType.DeclAssignStmt;
        this.start_line = start_line;
        this.start_col = start_col;
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

    public BlockStmt(List<Stmt> statements, int start_line, int start_col) {
        this.statements = statements;
        this.s_type = StmtType.BlockStmt;
        this.start_line = start_line;
        this.start_col = start_col;
    }

    public BlockStmt(int start_line, int start_col) {
        this(new ArrayList<>(), start_line, start_col);
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

//funcDefns are for program files
class FuncDefn extends Node implements Printable {
    private String name;
    private List<Pair<String, Type>> params;
    private Stmt body;
    private List<Type> output;

    public FuncDefn(String name, List<Pair<String, Type>> params, List<Type> output, Stmt body,
                    int start_line, int start_col) {
        this.name = name;
        this.params = params;
        this.output = output;
        this.body = body;
        this.start_line = start_line;
        this.start_col = start_col;
    }

    public FuncDefn(String name, List<Pair<String, Type>> params, Stmt body, int start_line, int start_col) {
        this.name = name;
        this.params = params;
        this.body = body;
        this.output = new ArrayList<Type>();
        this.start_line = start_line;
        this.start_col = start_col;
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

class UseInterface extends Node{
    private String name;

    public UseInterface(String name, int start_line, int start_col) {
        this.name = name;
        this.start_line = start_line;
        this.start_col = start_col;
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

//funcDecls are for interfaces
class FuncDecl extends Node implements Printable {
    private String name;
    private List<Pair<String, Type>> params;
    private List<Type> output;

    public FuncDecl(String name, List<Pair<String, Type>> params, List<Type> output, int start_line, int start_col) {
        this.name = name;
        this.params = params;
        this.output = output;
        this.start_line = start_line;
        this.start_col = start_col;
    }

    public FuncDecl(String name, List<Pair<String, Type>> params, int start_line, int start_col) {
        this.name = name;
        this.params = params;
        this.output = new ArrayList<Type>();
        this.start_line = start_line;
        this.start_col = start_col;
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
abstract class SourceFile extends Node implements Printable {
    abstract boolean isInterface();
}

class InterfaceFile extends SourceFile {
    private ArrayList<FuncDecl> funcDecls;

    public InterfaceFile(List<FuncDecl> funcDecls, int start_line, int start_col) {
        this.funcDecls = new ArrayList<>(funcDecls);
        this.start_line = start_line;
        this.start_col = start_col;
    }

    public List<FuncDecl> getFuncDecls() {
        return funcDecls;
    }

    public void addFuncDecl(FuncDecl funcDecl) {
        funcDecls.add(funcDecl);
    }

    public boolean isInterface() {
        return true;
    }

    public void prettyPrint(CodeWriterSExpPrinter w) {
        w.startUnifiedList();
        w.startUnifiedList();
        funcDecls.forEach((i) -> i.prettyPrint(w));
        w.endList();
        w.endList();
    }
}

class ProgramFile extends SourceFile {
    private ArrayList<UseInterface> imports;
    private ArrayList<FuncDefn> funcDefns;

    public ProgramFile(List<UseInterface> imports, List<FuncDefn> funcDefns, int start_line, int start_col) {
        this.imports = new ArrayList<>(imports);
        this.funcDefns = new ArrayList<>(funcDefns);
        this.start_line = start_line;
        this.start_col = start_col;
    }

    public List<UseInterface> getImports() {
        return imports;
    }

    public List<FuncDefn> getFuncDefns() {
        return funcDefns;
    }

    public void addFuncDefn(FuncDefn funcDefn) {
        funcDefns.add(funcDefn);
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
        funcDefns.forEach((d) -> d.prettyPrint(w));
        w.endList();
        w.endList();
    }
}

