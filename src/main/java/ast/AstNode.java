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

public abstract class Type implements Printable {
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
    private Character raw;
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
        this.raw = val;
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
    private String raw;

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
        this.raw = value;
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

