package edu.cornell.cs.cs4120.xic.ir;

import asm.ASMInstr;
import asm.ASMExprTemp;
import edu.cornell.cs.cs4120.xic.ir.visit.ASMTranslationVisitor;

import java.util.List;

public interface IRExpr extends IRNode {
    boolean isConstant();

    long constant();

    List<ASMInstr> accept(ASMTranslationVisitor v, ASMExprTemp t);
}
