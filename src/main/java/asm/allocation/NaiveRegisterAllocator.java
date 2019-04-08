package asm.allocation;
import asm.ASMInstr;

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
        return 0; //TODO
    }
}
