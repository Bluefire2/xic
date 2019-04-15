package edu.cornell.cs.cs4120.xic.ir;

import asm.ASMInstr;
import edu.cornell.cs.cs4120.xic.ir.visit.ASMTranslationVisitor;

import java.util.List;

/**
 * An intermediate representation for statements
 */
public abstract class IRStmt extends IRNode_c {
    public abstract List<ASMInstr> accept(ASMTranslationVisitor v);
}
