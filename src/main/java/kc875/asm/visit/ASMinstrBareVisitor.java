package kc875.asm.visit;

import kc875.asm.ASMInstrLabel;
import kc875.asm.ASMInstr_0Arg;
import kc875.asm.ASMInstr_1Arg;
import kc875.asm.ASMInstr_2Arg;

/**
 * Framework to _barely_ visit ASMInstr nodes, i.e., only the node is visited,
 * not the children.
 */
public interface ASMinstrBareVisitor<T> {
    T visit(ASMInstrLabel i);
    T visit(ASMInstr_0Arg i);
    T visit(ASMInstr_1Arg i);
    T visit(ASMInstr_2Arg i);
}
