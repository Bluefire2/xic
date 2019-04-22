package edu.cornell.cs.cs4120.xic.ir.visit;

import edu.cornell.cs.cs4120.xic.ir.*;
import kc875.asm.ASMExprRegReplaceable;

/**
 * Framework to _barely_ visit IRNodes, i.e., only the node is visited, not
 * the children.
 */
interface IRBareVisitor<T> {
    T visit(IRBinOp node, ASMExprRegReplaceable destreg);
    T visit(IRCall node, ASMExprRegReplaceable destreg);
    T visit(IRCJump node);
    T visit(IRCompUnit node);
    T visit(IRConst node, ASMExprRegReplaceable destreg);
    T visit(IRExp node);
    T visit(IRFuncDecl node);
    T visit(IRJump node);
    T visit(IRLabel node);
    T visit(IRMem node, ASMExprRegReplaceable destreg);
    T visit(IRMove node);
    T visit(IRReturn node);
    T visit(IRSeq node);
    T visit(IRTemp node, ASMExprRegReplaceable destreg);
}
