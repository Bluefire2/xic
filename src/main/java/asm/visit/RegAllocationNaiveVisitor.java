package asm.visit;

import asm.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class RegAllocationNaiveVisitor extends RegAllocationVisitor {
    HashMap<String, Integer> tempMap;


    public RegAllocationNaiveVisitor() {
        tempMap = new HashMap<>();
    }

    public List<ASMInstr> allocate(List<ASMInstr> input){
        List<ASMInstr> instrs = new ArrayList<>();
        int num_temps = count_temps(input);
        instrs.add(new ASMInstr_2Arg(
                ASMOpCode.ADD,
                new ASMExprReg("rsp"),
                new ASMExprConst(num_temps * 8))
        );
        //Unused regs - rbx, r7, r10-r15
        //initial SP moved to rbx
        instrs.add(new ASMInstr_2Arg(
                ASMOpCode.MOV,
                new ASMExprReg("rbx"),
                new ASMExprReg("rsp"))
        );
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

    @Override
    public List<ASMInstr> visit(ASMInstrLabel i) {
        List<ASMInstr> l = new ArrayList<>();
        l.add(i);
        return l;
    }

    @Override
    public List<ASMInstr> visit(ASMInstr_0Arg i) {
        return null;
    }

    @Override
    public List<ASMInstr> visit(ASMInstr_1Arg i) {
        return null;
    }

    @Override
    public List<ASMInstr> visit(ASMInstr_2Arg i) {
        return null;
    }
}
