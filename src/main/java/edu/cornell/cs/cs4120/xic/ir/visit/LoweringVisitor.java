package edu.cornell.cs.cs4120.xic.ir.visit;

import edu.cornell.cs.cs4120.xic.ir.*;
import edu.cornell.cs.cs4120.xic.ir.visit.IRVisitor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class LoweringVisitor extends IRVisitor {

    private class BasicBlock {
        List<IRNode> statements;
        BasicBlock next;  //TODO: possibly make list

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
        if (node instanceof IRLabel && current != head) {
            BasicBlock newblock = new BasicBlock();
            newblock.addStmt(node);
            current.setNext(newblock);
            current = newblock;
        } else if (node instanceof IRReturn || node instanceof IRJump || node instanceof IRCJump) {
            current.addStmt(node);
            BasicBlock newblock = new BasicBlock();
            current.setNext(newblock);
            current = newblock;
        } else {
            current.addStmt(node);
        }
    }

    private Collection<String> getTemps(IRExpr e) {
        //TODO
        return null;
    }

    private boolean ifExprsCommute(IRExpr e1, IRExpr e2) {
        //TODO
        return false;
    }

    @Override
    public IRNode leave(IRNode parent, IRNode n, IRNode n_, IRVisitor v_) {
        if (n_ instanceof IRBinOp) return lower((IRBinOp) n_);
        if (n_ instanceof IRCall) return lower((IRCall) n_);
        if (n_ instanceof IRCJump) return lower((IRCJump) n_);
        if (n_ instanceof IRCompUnit) return lower((IRCompUnit) n_);
        if (n_ instanceof IRConst) return lower((IRConst) n_);
        if (n_ instanceof IRESeq) return lower((IRESeq) n_);
        if (n_ instanceof IRExp) return lower((IRExp) n_);
        if (n_ instanceof IRFuncDecl) return lower((IRFuncDecl) n_);
        if (n_ instanceof IRJump) return lower((IRJump) n_);
        if (n_ instanceof IRLabel) return lower((IRLabel) n_);
        if (n_ instanceof IRMem) return lower((IRMem) n_);
        if (n_ instanceof IRMove) return lower((IRMove) n_);
        if (n_ instanceof IRName) return lower((IRName) n_);
        if (n_ instanceof IRReturn) return lower((IRReturn) n_);
        if (n_ instanceof IRSeq) return lower((IRSeq) n_);
        if (n_ instanceof IRTemp) return lower((IRTemp) n_);
        else return null; //Illegal state
    }

    public IRNode lower(IRBinOp irnode) {
        //TODO
        return irnode;

    }

    public IRNode lower(IRCall irnode) {
        //TODO
        return irnode;
    }

    public IRNode lower(IRCJump irnode) {
        //TODO
        return irnode;
    }

    public IRNode lower(IRCompUnit irnode) {
        //TODO
        return irnode;
    }

    public IRNode lower(IRConst irnode) {
        //Constants are already canonical
        return irnode;
    }

    public IRNode lower(IRESeq irnode) {
        IRNode visited = irnode.visitChildren(this);
        addNodeToBlock(visited);
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

    public IRNode lower(IRExp irnode) {
        IRNode visited = irnode.visitChildren(this);
        return visited;
    }

    public IRNode lower(IRFuncDecl irnode) {
        //TODO
        return irnode;
    }

    public IRNode lower(IRJump irnode) {
        //TODO
        return irnode;
    }

    public IRNode lower(IRLabel irnode) {
        return irnode;
    }

    public IRNode lower(IRMem irnode) {
        IRNode visited = irnode.visitChildren(this);
        return visited;
    }

    public IRNode lower(IRMove irnode) {
        //TODO
        return irnode;
    }

    public IRNode lower(IRName irnode) {
        //Names are already canonical
        return irnode;
    }

    public IRNode lower(IRReturn irnode) {
        //TODO
        return irnode;
    }

    public IRNode lower(IRSeq irnode) {
        //TODO
        return irnode;
    }

    public IRNode lower(IRTemp irnode) {
        //Temps are already canonical
        return irnode;
    }



}
