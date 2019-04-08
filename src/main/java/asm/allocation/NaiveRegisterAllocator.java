package asm.allocation;
import asm.*;
import edu.cornell.cs.cs4120.xic.ir.visit.ASMTranslationVisitor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class NaiveRegisterAllocator {
    HashMap<String, Integer> tempMap;


    public NaiveRegisterAllocator() {
        tempMap = new HashMap<>();
    }

    public List<ASMInstr> allocate(List<ASMInstr> input){
        List<ASMInstr> instrs = new ArrayList<>();
        //TODO
        //count regs
        //increment RSP
        //MOV RSP to some register
        //generate code for instrs, modifying the tempMap
        return instrs;
    }

    public int count_temps(List<ASMInstr> input){
        int count = 0;
        for (ASMInstr instr : input) {
            if (instr instanceof ASMInstr_2Arg) {
                ASMInstr_2Arg i2a = (ASMInstr_2Arg) instr;
                if (i2a.getOpCode() == ASMOpCode.MOV) {
                    ASMExpr dest = i2a.getDest();
                    if (dest instanceof ASMExprTemp) count++;
                }
            }
        }
        return count;
    }
}
