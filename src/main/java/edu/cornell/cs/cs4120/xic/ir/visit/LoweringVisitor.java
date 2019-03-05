package edu.cornell.cs.cs4120.xic.ir.visit;

import edu.cornell.cs.cs4120.xic.ir.*;

public class LoweringVisitor extends IRVisitor {

    public LoweringVisitor(IRNodeFactory inf) {
        super(inf);
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
        //TODO
        return irnode;
    }

    public IRNode visit(IRESeq irnode) {
        IRStmt stmt = irnode.stmt();
        IRExp exp = new IRExp(irnode.expr());
        return new IRSeq(stmt, exp);
    }

    public IRNode visit(IRExp irnode) {
        //TODO
        return irnode;
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
        //TODO
        return irnode;
    }

    public IRNode visit(IRMem irnode) {
        //TODO
        return irnode;
    }

    public IRNode visit(IRMove irnode) {
        //TODO
        return irnode;
    }

    public IRNode visit(IRName irnode) {
        //TODO
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
        //TODO
        return irnode;
    }



}
