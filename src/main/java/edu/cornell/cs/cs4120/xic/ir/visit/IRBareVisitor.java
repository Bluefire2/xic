package edu.cornell.cs.cs4120.xic.ir.visit;

import edu.cornell.cs.cs4120.xic.ir.*;
import asm.*;

/**
 * Framework to _barely_ visit IRNodes, i.e., only the node is visited, not
 * the children.
 */
interface IRBareVisitor<T> {
    T visit(IRBinOp node, ASMExprTemp destreg);
    T visit(IRCall node, ASMExprTemp destreg);
    T visit(IRCJump node);
    T visit(IRCompUnit node);
    T visit(IRConst node, ASMExprTemp destreg);
    T visit(IRExp node);
    T visit(IRFuncDecl node);
    T visit(IRJump node);
    T visit(IRLabel node);
    T visit(IRMem node, ASMExprTemp destreg);
    T visit(IRMove node);
    T visit(IRReturn node);
    T visit(IRSeq node);
    T visit(IRTemp node, ASMExprTemp destreg);
}
