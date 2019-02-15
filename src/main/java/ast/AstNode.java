package ast;

import edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;
import org.apache.commons.lang3.StringEscapeUtils;
import polyglot.util.Pair;
import xi_parser.Printable;

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

    IfElseStmt(Expr guard, Stmt thenStmt, Stmt elseStmt) {
        this.guard = guard;
        this.thenStmt = thenStmt;
        this.elseStmt = elseStmt;
        this.s_type = StmtType.IfElseStmt;
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
        w.printAtom("while");
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

abstract class Assignable implements Printable {

}

class UnderscoreAssignable extends Assignable {
    @Override
    public void prettyPrint(CodeWriterSExpPrinter w) {
        w.printAtom("_");
    }
}

class IdAssignable extends Assignable {
    private Expr id;

    IdAssignable(Expr id) {
        this.id = id;
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

    IndexAssignable(Expr index) {
        this.index = index;
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

    public AssignStmt(List<Assignable> left, List<Expr> right) {
        this.left = left;
        this.right = right;
        this.s_type = StmtType.AssignStmt;
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

    public DeclStmt(List<Pair<String, Type>> decls) {
        this.decls = decls;
        this.s_type = StmtType.DeclStmt;
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
        this.s_type = StmtType.DeclAssignStmt;
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

//funcDefns are for program files
class FuncDefn implements Printable {
    private String name;
    private List<Pair<String, Type>> params;
    private Stmt body;
    private List<Type> output;

    public FuncDefn(String name, List<Pair<String, Type>> params, List<Type> output, Stmt body) {
        this.name = name;
        this.params = params;
        this.output = output;
        this.body = body;
    }

    public FuncDefn(String name, List<Pair<String, Type>> params, Stmt body) {
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

//funcDecls are for interfaces
class FuncDecl implements Printable {
    private String name;
    private List<Pair<String, Type>> params;
    private List<Type> output;

    public FuncDecl(String name, List<Pair<String, Type>> params, List<Type> output) {
        this.name = name;
        this.params = params;
        this.output = output;
    }

    public FuncDecl(String name, List<Pair<String, Type>> params) {
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
    private ArrayList<FuncDecl> funcDecls;

    public InterfaceFile(List<FuncDecl> funcDecls) {
        this.funcDecls = new ArrayList<>(funcDecls);
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

    public ProgramFile(List<UseInterface> imports, List<FuncDefn> funcDefns) {
        this.imports = new ArrayList<>(imports);
        this.funcDefns = new ArrayList<>(funcDefns);
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

