package edu.cornell.cs.cs4120.xic.ir;

import edu.cornell.cs.cs4120.xic.ir.visit.ASMTranslationVisitor;
import kc875.asm.ASMInstr;

import java.util.List;

/**
 * An intermediate representation for statements
 */
public abstract class IRStmt extends IRNode_c {
    public abstract List<ASMInstr> accept(ASMTranslationVisitor v);
}
