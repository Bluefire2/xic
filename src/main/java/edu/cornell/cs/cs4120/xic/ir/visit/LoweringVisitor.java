package edu.cornell.cs.cs4120.xic.ir.visit;

import edu.cornell.cs.cs4120.xic.ir.*;

public class LoweringVisitor extends IRVisitor {

    public LoweringVisitor(IRNodeFactory inf) {
        super(inf);
    }

    private boolean ifExprsCommute(IRExpr e1, IRExpr e2) {
        //TODO
        return false;
    }

    public IRNode visit(IRBinOp irnode) {
        //TODO
        return irnode;

    }

    public IRNode visit(IRCall irnode) {
        //TODO
        return irnode;
    }

    public IRNode visit(IRCJump irnode) {
        //TODO
        return irnode;
    }

    public IRNode visit(IRCompUnit irnode) {
        //TODO
        return irnode;
    }

    public IRNode visit(IRConst irnode) {
        //Constants are already canonical
        return irnode;
    }

    public IRNode visit(IRESeq irnode) {
        IRNode visited = irnode.visitChildren(this);
        if (visited instanceof IRESeq) {
            IRESeq newEseq = (IRESeq) visited;
            IRStmt stmt = newEseq.stmt();
            IRExp exp = new IRExp(newEseq.expr());
            return new IRSeq(stmt, exp);
        }
        else {
            return visited;
        }
    }

    public IRNode visit(IRExp irnode) {
        IRNode visited = irnode.visitChildren(this);
        return visited;
    }

    public IRNode visit(IRFuncDecl irnode) {
        //TODO
        return irnode;
    }

    public IRNode visit(IRJump irnode) {
        //TODO
        return irnode;
    }

    public IRNode visit(IRLabel irnode) {
        return irnode;
    }

    public IRNode visit(IRMem irnode) {
        IRNode visited = irnode.visitChildren(this);
        return visited;
    }

    public IRNode visit(IRMove irnode) {
        //TODO
        return irnode;
    }

    public IRNode visit(IRName irnode) {
        //Names are already canonical
        return irnode;
    }

    public IRNode visit(IRReturn irnode) {
        //TODO
        return irnode;
    }

    public IRNode visit(IRSeq irnode) {
        //TODO
        return irnode;
    }

    public IRNode visit(IRTemp irnode) {
        //Temps are already canonical
        return irnode;
    }



}
