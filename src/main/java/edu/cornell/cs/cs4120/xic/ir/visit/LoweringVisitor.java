package edu.cornell.cs.cs4120.xic.ir.visit;

import edu.cornell.cs.cs4120.xic.ir.*;

import java.util.ArrayList;
import java.util.List;

public class LoweringVisitor extends IRVisitor {

    private class BasicBlock {
        List<IRNode> statements;
        BasicBlock next;

        BasicBlock () {
            statements = new ArrayList<>();
            next = null;
        }

        void addStmt(IRNode stmt) {
            statements.add(stmt);
        }

        BasicBlock getNext() {
            return next;
        }

        void setNext(BasicBlock next) {
            this.next = next;
        }
    }

    BasicBlock head;
    BasicBlock current;

    public LoweringVisitor(IRNodeFactory inf) {
        super(inf);
        head = new BasicBlock();
        current = head;
    }

    private void addNodeToBlock(IRNode node) {
        if(node instanceof IRLabel && current != head) {
            BasicBlock newblock = new BasicBlock();
            newblock.addStmt(node);
            current.setNext(newblock);
            current = newblock;
        }
        else if (node instanceof IRReturn || node instanceof IRJump || node instanceof IRCJump) {
            current.addStmt(node);
            BasicBlock newblock = new BasicBlock();
            current.setNext(newblock);
            current = newblock;
        }
        else {
            current.addStmt(node);
        }
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
