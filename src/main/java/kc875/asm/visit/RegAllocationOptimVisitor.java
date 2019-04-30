package kc875.asm.visit;

import kc875.asm.ASMInstr;
import kc875.asm.ASMUtils;
import kc875.asm.visit.RegAllocationNaiveVisitor.NaiveSpillMode;

import java.util.List;


public class RegAllocationOptimVisitor {
    public enum SpillMode {
        Reserve,//r13,14,15 reserved for spilling
        Restore //can use any register
    }
    static SpillMode spillMode;

    public RegAllocationOptimVisitor(SpillMode s) {
        spillMode = s;
    }

    public List<ASMInstr> allocate(List<ASMInstr> i) {
        List<ASMInstr> instrs = i;
        instrs = ASMUtils.execPerFunc(instrs, this::allocateFunc);
        NaiveSpillMode s;
        if (spillMode == SpillMode.Reserve) {
            s = NaiveSpillMode.Reserved;
        } else {
            s = NaiveSpillMode.Restore;
        }
        RegAllocationNaiveVisitor v = new RegAllocationNaiveVisitor(false, s);
        return v.allocate(instrs);
    }

    private List<ASMInstr> allocateFunc(List<ASMInstr> instrs){
        RegAllocationColoringVisitor v = new RegAllocationColoringVisitor(spillMode);
        return v.allocate(instrs);
    }
}
