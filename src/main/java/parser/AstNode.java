package parser;

import java.util.*;

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
}

class Tvar extends Type {
    private String name;

    Tvar(String name) {
        this.name = name;
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
    private Integer length;

    ListType(Type type) {
        this.contentsType = type;
    }

    public String toString() {
        return contentsType.toString() + " list";
    }

    public Type getContentsType() {
        return contentsType;
    }

    public Integer getLength() {
        return length;
    }
}

class TupleType extends Type {
    private Type[] contentsTypes;

    TupleType(Type[] types) {
        this.contentsTypes = types;
    }

    public String toString() {
        List<String> typeNames = new ArrayList<>();
        Arrays.asList(contentsTypes).forEach(elt -> typeNames.add(elt.toString()));
        return String.join(" * ", typeNames);
    }

    public Type[] getContentsTypes() {
        return contentsTypes;
    }
}


abstract class Expr {
    private Type type; //unused for now
}

class BinopExpr extends Expr {
    private String op;
    private Expr left;
    private Expr right;

    BinopExpr(String op, Expr left, Expr right) {
        this.op = op;
        this.left = left;
        this.right = right;
    }

    public String getOp() {
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
    private String op;
    private Expr expr;

    UnopExpr(String op, Expr left, Expr right) {
        this.op = op;
        this.expr = expr;
    }

    public String getOp() {
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
    }

    public String getName() {
        return name;
    }
}

class IntLiteralExpr extends Expr {
    private Integer value;

    IntLiteralExpr(int val) {
        this.value = value;
    }

    public Integer getValue() {
        return value;
    }
}

class BoolLiteralExpr extends Expr {
    private Boolean value;

    BoolLiteralExpr(boolean val) {
        this.value = value;
    }

    public Boolean getValue() {
        return value;
    }
}

class ListLiteralExpr extends Expr {
    private Expr[] contents;

    ListLiteralExpr(Expr[] contents) {
        this.contents = contents;
    }

    public Expr[] getContents() {
        return contents;
    }

    public int getLength() {
        return contents.length;
    }
}

class IndexExpr extends Expr {
    private Expr list;
    private Expr index;

    IndexExpr(Expr list, Expr index) {
        this.list = list;
        this.index = index;
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
    private Expr[] args;

    FunctionCallExpr(String name, Expr[] args) {
        this.name = name;
        this.args = args;
    }

    public String getName() {
        return name;
    }

    public Expr[] getArgs() {
        return args;
    }
}

abstract class Stmt {
}

class FunctionReturnStmt extends Stmt {
    private Expr returnVal;

    FunctionReturnStmt(Expr returnVal) {
        this.returnVal = returnVal;
    }

    public Expr getReturnVal() {
        return returnVal;
    }
}

class ProcedureReturnStmt extends Stmt {
    ProcedureReturnStmt() {
    }
}

class IfStmt extends Stmt {
    private Expr guard;
    private Stmt thenStmt;

    IfStmt(Expr guard, Stmt thenStmt) {
        this.guard = guard;
        this.thenStmt = thenStmt;
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
    }
}

class WhileStmt extends Stmt {
    private Expr guard;
    private Stmt doStmt;

    WhileStmt(Expr guard, Stmt doStmt) {
        this.guard = guard;
        this.doStmt = doStmt;
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
    private Expr[] args;

    public ProcedureCallStmt(String name, Expr[] args) {
        this.name = name;
        this.args = args;
    }

    public String getName() {
        return name;
    }

    public Expr[] getArgs() {
        return args;
    }
}

class AssignStmt extends Stmt {
    private Expr left;
    private Expr right;

    public AssignStmt(Expr left, Expr right) {
        this.left = left;
        this.right = right;
    }

    public Expr getLeft() {
        return left;
    }

    public Expr getRight() {
        return right;
    }
}

class MultiAssignStmt extends Stmt {
    private Expr[] left;
    private Expr[] right;

    public MultiAssignStmt(Expr[] left, Expr[] right) {
        this.left = left;
        this.right = right;
    }

    public Expr[] getLeft() {
        return left;
    }

    public Expr[] getRight() {
        return right;
    }
}


class DeclStmt extends Stmt {
    private Pair<String, Type> decl;

    public DeclStmt(Pair<String, Type> decl) {
        this.decl = decl;
    }

    public Pair<String, Type> getDecl() {
        return decl;
    }
}

class MultiDeclStmt extends Stmt {
    private Pair<String, Type>[] decls;

    public MultiDeclStmt(Pair<String, Type>[] decls) {
        this.decls = decls;
    }

    public Pair<String, Type>[] getDecls() {
        return decls;
    }
}

class BlockStmt extends Stmt {
    private Stmt[] statements;

    public BlockStmt(Stmt[] statements) {
        this.statements = statements;
    }

    public BlockStmt() {    //empty block
    }

    public Stmt[] getStatments() {
        return statements;
    }

    public boolean isEmpty(){
        return statements.length == 0;
    }

    public Stmt getLastStatement(){
        return statements[statements.length-1];
    }
}

//definitions are for program files
abstract class Defn {
}

class FunctionDefn extends Defn {
    private String name;
    private Pair<String, Type>[] params;
    private Type output;
    private Stmt body;

    public FunctionDefn(String name, Pair<String, Type>[] params, Type output, Stmt body) {
        this.name = name;
        this.params = params;
        this.output = output;
        this.body = body;
    }

    public String getName() {
        return name;
    }

    public Pair<String, Type>[] getParams() {
        return params;
    }

    public Type getOutput() {
        return output;
    }

    public Stmt getBody() {
        return body;
    }
}

class ProcedureDefn extends Defn {
    private String name;
    private Pair<String, Type>[] params;
    private Stmt body;

    public ProcedureDefn(String name, Pair<String, Type>[] params, Stmt body) {
        this.name = name;
        this.params = params;
        this.body = body;
    }

    public String getName() {
        return name;
    }

    public Pair<String, Type>[] getParams() {
        return params;
    }

    public Stmt getBody() {
        return body;
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
}

class FunctionDecl extends Decl {
    private String name;
    private Pair<String, Type>[] params;
    private Type output;

    public FunctionDecl(String name, Pair<String, Type>[] params, Type output) {
        this.name = name;
        this.params = params;
        this.output = output;
    }

    public String getName() {
        return name;
    }

    public Pair<String, Type>[] getParams() {
        return params;
    }

    public Type getOutput() {
        return output;
    }
}

class ProcedureDecl extends Decl {
    private String name;
    private Pair<String, Type>[] params;

    public ProcedureDecl(String name, Pair<String, Type>[] params) {
        this.name = name;
        this.params = params;
    }

    public String getName() {
        return name;
    }

    public Pair<String, Type>[] getParams() {
        return params;
    }
}

//top level "nodes"
class InterfaceFile {
    private Decl[] declarations;

    public InterfaceFile(Decl[] declarations) {
        this.declarations = declarations;
    }

    public Decl[] getDeclarations() {
        return declarations;
    }
}

class ProgramFile {
    private UseInterface[] imports;
    private Defn[] definitions;

    public ProgramFile(UseInterface[] imports, Defn[] definitions) {
        this.imports = imports;
        this.definitions = definitions;
    }

    public UseInterface[] getImports() {
        return imports;
    }

    public Defn[] getDefinitions() {
        return definitions;
    }
}
