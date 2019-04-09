package asm.visit;

import asm.ASMInstrLabel;
import asm.ASMInstr_0Arg;
import asm.ASMInstr_1Arg;
import asm.ASMInstr_2Arg;

/**
 * Framework to _barely_ visit ASMInstr nodes, i.e., only the node is visited,
 * not the children.
 */
interface ASMinstrBareVisitor<T> {
    T visit(ASMInstrLabel i);
    T visit(ASMInstr_0Arg i);
    T visit(ASMInstr_1Arg i);
    T visit(ASMInstr_2Arg i);
}
