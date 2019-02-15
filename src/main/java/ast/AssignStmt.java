package ast;

import edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;

import java.util.List;

public class AssignStmt extends Stmt {
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
