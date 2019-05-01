package kc875.asm.visit;

import kc875.asm.visit.RegAllocationOptimVisitor.SpillMode;
import kc875.asm.*;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.*;

public class RegAllocationColoringVisitorTest {

    /*
    n is the number of unique temps
    returns instructions with n variables concurrently live
    format of instructions:
    MOV n 1, for all n
    followed by
    ADD [n] n, for all n
    */
    private List<ASMInstr> getTestInstrs(int n) {
        List<ASMInstr> instrs = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            String tname = String.format("_temp_%d", i);
            instrs.add(new ASMInstr_2Arg(
                    ASMOpCode.MOV,
                    new ASMExprTemp(tname),
                    new ASMExprConst(1))
            );
        }
        for (int i = 0; i < n; i++) {
            String tname = String.format("_temp_%d", i);
            instrs.add(new ASMInstr_2Arg(
                    ASMOpCode.ADD,
                    new ASMExprMem(new ASMExprTemp(tname)),
                    new ASMExprTemp(tname))
            );
        }
        return instrs;
    }

    /*
    n is the number of unique temps
    returns instructions with n+3 variables concurrently live
    format of instructions:
    MOV r9 1
    MOV r10 1
    MOV r11 1
    MOV tn 2, for all n
    MOV r9 t0
    MOV r10 t0
    MOV r11 t0
    MOV [tn] tn, for all n
    */
    public static List<ASMInstr> getTestInstrsPrecolored(int n) {
        List<ASMInstr> instrs = new ArrayList<>();
        String[] precolored = {"r9", "r10", "r11"};
        for (String r : precolored) {
            instrs.add(new ASMInstr_2Arg(
                    ASMOpCode.MOV,
                    new ASMExprReg(r),
                    new ASMExprConst(1))
            );
        }
        for (int i = 0; i < n; i++) {
            String tname = String.format("_temp_%d", i);
            instrs.add(new ASMInstr_2Arg(
                    ASMOpCode.MOV,
                    new ASMExprTemp(tname),
                    new ASMExprConst(2))
            );
        }
        for (String r : precolored) {
            instrs.add(new ASMInstr_2Arg(
                    ASMOpCode.ADD,
                    new ASMExprReg(r),
                    new ASMExprTemp(String.format("_temp_%d", 0))
            ));
        }
        for (int i = 0; i < n; i++) {
            String tname = String.format("_temp_%d", i);
            instrs.add(new ASMInstr_2Arg(
                    ASMOpCode.MOV,
                    new ASMExprMem(new ASMExprTemp(tname)),
                    new ASMExprTemp(tname)
            ));
        }
        return instrs;
    }

    /*
    returns instructions that test coalescing
    format of instructions:
    MOV tn 1 for n = 0...8
    ADD t0 tn for n = 1...8
    MOV tn 2 for n = 9...16
    ADD t0 tn for n = 9...16
    only 8 instructions are live at any given time
    */
    private List<ASMInstr> getTestNotAllLiveInstrs() {
        List<ASMInstr> instrs = new ArrayList<>();
        for (int i = 0; i < 9; i++) {
            String tname = String.format("_temp_%d", i);
            instrs.add(new ASMInstr_2Arg(
                    ASMOpCode.MOV,
                    new ASMExprTemp(tname),
                    new ASMExprConst(1))
            );
        }
        for (int i = 1; i < 9; i++) {
            String tname = String.format("_temp_%d", i);
            instrs.add(new ASMInstr_2Arg(
                    ASMOpCode.ADD,
                    new ASMExprTemp(String.format("_temp_%d", 0)),
                    new ASMExprTemp(tname))
            );
        }
        for (int i = 9; i < 17; i++) {
            String tname = String.format("_temp_%d", i);
            instrs.add(new ASMInstr_2Arg(
                    ASMOpCode.MOV,
                    new ASMExprTemp(tname),
                    new ASMExprConst(2))
            );
        }
        for (int i = 9; i < 17; i++) {
            String tname = String.format("_temp_%d", i);
            instrs.add(new ASMInstr_2Arg(
                    ASMOpCode.ADD,
                    new ASMExprTemp(String.format("_temp_%d", 0)),
                    new ASMExprTemp(tname))
            );
        }
        return instrs;
    }


    /*
    returns instructions that test coalescing
    format of instructions:
    MOV t0 1
    MOV t1 1
    followed by
    MOV tn t0
    ADD t1 tn
    for n=2...15
    */
    private List<ASMInstr> getTestCoalesceInstructions() {
        List<ASMInstr> instrs = new ArrayList<>();
        String tname = String.format("_temp_%d", 0);
        instrs.add(new ASMInstr_2Arg(
                ASMOpCode.MOV,
                new ASMExprTemp(tname),
                new ASMExprConst(1))
        );

        tname = String.format("_temp_%d", 1);
        instrs.add(new ASMInstr_2Arg(
                ASMOpCode.MOV,
                new ASMExprTemp(tname),
                new ASMExprConst(1))
        );

        for (int i = 2; i < 16; i++) {
            tname = String.format("_temp_%d", i);
            instrs.add(new ASMInstr_2Arg(
                    ASMOpCode.MOV,
                    new ASMExprTemp(tname),
                    new ASMExprTemp(String.format("_temp_%d", 0))
            ));
            instrs.add(new ASMInstr_2Arg(
                    ASMOpCode.ADD,
                    new ASMExprTemp(String.format("_temp_%d", 1)),
                    new ASMExprTemp(tname)
            ));
        }
        return instrs;
    }

    /*
    returns instructions that test not coalescing
    format of instructions:
    MOV t0 1
    followed by
    MOV tn t0
    ADD t0 tn
    for n=1...15
    */
    private List<ASMInstr> getTestDoNotCoalesceInstructions() {
        List<ASMInstr> instrs = new ArrayList<>();
        String tname = String.format("_temp_%d", 0);
        instrs.add(new ASMInstr_2Arg(
                ASMOpCode.MOV,
                new ASMExprTemp(tname),
                new ASMExprConst(1))
        );
        for (int i = 1; i < 16; i++) {
            tname = String.format("_temp_%d", i);
            instrs.add(new ASMInstr_2Arg(
                    ASMOpCode.MOV,
                    new ASMExprTemp(tname),
                    new ASMExprTemp(String.format("_temp_%d", 0))
            ));

        }
        for (int j = 1; j < 16; j++) {
            tname = String.format("_temp_%d", j);
            instrs.add(new ASMInstr_2Arg(
                    ASMOpCode.ADD,
                    new ASMExprTemp(String.format("_temp_%d", 0)),
                    new ASMExprTemp(tname)
            ));
        }
        return instrs;
    }

    private int countTemps(List<ASMInstr> instrs) {
        int count = 0;
        for (ASMInstr i : instrs) {
            System.out.println(i.toString());
            if (i instanceof ASMInstr_1Arg) {
                count += countTempsExpr(((ASMInstr_1Arg) i).getArg());
            } else if (i instanceof ASMInstr_2Arg) {
                ASMInstr_2Arg i2 = (ASMInstr_2Arg) i;
                count += countTempsExpr(i2.getDest());
                count += countTempsExpr(i2.getSrc());
            }
        }
        return count;
    }

    private int countTempsExpr(ASMExpr e) {
        if (e instanceof ASMExprBinOp) {
            ASMExprBinOp b = (ASMExprBinOp) e;
            return countTempsExpr(b.getLeft()) + countTempsExpr(b.getRight());
        } else if (e instanceof ASMExprTemp) {
            return 1;
        } else if (e instanceof ASMExprMem) {
            return countTempsExpr(((ASMExprMem) e).getAddr());
        } else {
            return 0;
        }
    }

    //gets set of temps (not REGS!)
    private Set<ASMExprRT> getAllTemps(List<ASMInstr> instrs) {
        Set<ASMExprRT> temps = new HashSet<>();
        for (ASMInstr i : instrs) {
            if (i instanceof ASMInstr_1Arg) {
                temps.addAll(getTempsExpr(((ASMInstr_1Arg) i).getArg()));
            } else if (i instanceof ASMInstr_2Arg) {
                ASMInstr_2Arg i2 = (ASMInstr_2Arg) i;
                temps.addAll(getTempsExpr(i2.getDest()));
                temps.addAll(getTempsExpr(i2.getSrc()));
            }
        }
        return temps;
    }

    private Set<ASMExprRT> getTempsExpr(ASMExpr e) {
        Set<ASMExprRT> temps = new HashSet<>();
        if (e instanceof ASMExprBinOp) {
            ASMExprBinOp b = (ASMExprBinOp) e;
            temps.addAll(getTempsExpr(b.getLeft()));
            temps.addAll(getTempsExpr(b.getRight()));
        } else if (e instanceof ASMExprTemp) {
            temps.add((ASMExprTemp) e);
        } else if (e instanceof ASMExprMem) {
            temps.addAll(getTempsExpr(((ASMExprMem) e).getAddr()));
        }
        return temps;
    }

    @Test
    public void testBuildInvariantsReserve() {
        RegAllocationColoringVisitor visitor = new RegAllocationColoringVisitor(SpillMode.Reserve);
        List<ASMInstr> abstract_asm = getTestInstrs(15);
        visitor.buildInterferenceGraph(abstract_asm);
        assertTrue(visitor.checkDegreeInv());
        assertTrue(visitor.checkSimplifyWorklistInv());
        assertTrue(visitor.checkFreezeWorklistInv());
        assertTrue(visitor.checkSpillWorklistInv());
    }

    @Test
    public void testBuildInvariantsRestore() {
        List<ASMInstr> abstract_asm = getTestInstrs(15);
        RegAllocationColoringVisitor visitor = new RegAllocationColoringVisitor(SpillMode.Restore);
        visitor.buildInterferenceGraph(abstract_asm);
        assertTrue(visitor.checkDegreeInv());
        assertTrue(visitor.checkSimplifyWorklistInv());
        assertTrue(visitor.checkFreezeWorklistInv());
        assertTrue(visitor.checkSpillWorklistInv());
    }

    //test coloring for simple program with few live variables at a time
    @Test
    public void testColoringSimpleReserve() {
        //should successfully color 8 live vars
        RegAllocationColoringVisitor visitor = new RegAllocationColoringVisitor(SpillMode.Reserve);
        List<ASMInstr> abstract_asm = getTestInstrs(10);
        List<ASMInstr> colored = visitor.allocate(abstract_asm);
        assertEquals(countTemps(colored), 0);
    }

    //test coloring for simple program that requires spilling
    //TODO spilling too many temps
    @Test
    public void testColoringSimpleSpillReserve() {
        //should fail to color 12 live vars - 3 unique temps spilled
        RegAllocationColoringVisitor visitor = new RegAllocationColoringVisitor(SpillMode.Reserve);
        List<ASMInstr> abstract_asm = getTestInstrs(14);
        List<ASMInstr> colored = visitor.allocate(abstract_asm);
        assertEquals(getAllTemps(colored).size(), 3);
    }

    @Test
    public void testColoringSimpleRestore() {
        //should successfully color 10 live vars
        RegAllocationColoringVisitor visitor = new RegAllocationColoringVisitor(SpillMode.Restore);
        List<ASMInstr> abstract_asm = getTestInstrs(14);
        List<ASMInstr> colored = visitor.allocate(abstract_asm);
        assertEquals(countTemps(colored), 0);
    }

    @Test
    public void testColoringSimpleSpillRestore() {
        //should fail to color 15 live vars
        RegAllocationColoringVisitor visitor = new RegAllocationColoringVisitor(SpillMode.Restore);
        List<ASMInstr> abstract_asm = getTestInstrs(17);
        List<ASMInstr> colored = visitor.allocate(abstract_asm);
        assertEquals(getAllTemps(colored).size(), 3);
    }

    //lots of copies that can be coalesced
    @Test
    public void testColoringCoalesceReserve() {
        //should successfully color 8 live vars
        RegAllocationColoringVisitor visitor = new RegAllocationColoringVisitor(SpillMode.Reserve);
        List<ASMInstr> abstract_asm = getTestCoalesceInstructions();
        List<ASMInstr> colored = visitor.allocate(abstract_asm);
        assertEquals(countTemps(colored), 0);
    }

    @Test
    public void testColoringCoalesceRestore() {
        //should successfully color 10 live vars
        RegAllocationColoringVisitor visitor = new RegAllocationColoringVisitor(SpillMode.Restore);
        List<ASMInstr> abstract_asm = getTestCoalesceInstructions();
        List<ASMInstr> colored = visitor.allocate(abstract_asm);
        assertEquals(countTemps(colored), 0);
    }

    //lots of copies that cannot be coalesced
    @Test
    public void testColoringNotCoalesceReserve() {
        RegAllocationColoringVisitor visitor = new RegAllocationColoringVisitor(SpillMode.Reserve);
        List<ASMInstr> abstract_asm = getTestDoNotCoalesceInstructions();
        for (ASMInstr i : abstract_asm){
            System.out.println(i.toString());
        }
        List<ASMInstr> colored = visitor.allocate(abstract_asm);
        assertNotEquals(countTemps(colored), 0);
    }

    @Test
    public void testColoringNotCoalesceRestore() {
        RegAllocationColoringVisitor visitor = new RegAllocationColoringVisitor(SpillMode.Restore);
        List<ASMInstr> abstract_asm = getTestDoNotCoalesceInstructions();
        List<ASMInstr> colored = visitor.allocate(abstract_asm);
        assertNotEquals(countTemps(colored), 0);
    }

    //test coloring for program where not all regs are live at the same time (register reuse)
    @Test
    public void testColoringReuseReserve() {
        RegAllocationColoringVisitor visitor = new RegAllocationColoringVisitor(SpillMode.Reserve);
        List<ASMInstr> abstract_asm = getTestNotAllLiveInstrs();
        List<ASMInstr> colored = visitor.allocate(abstract_asm);
        assertEquals(countTemps(colored), 0);
    }

    @Test
    public void testColoringReuseRestore() {
        RegAllocationColoringVisitor visitor = new RegAllocationColoringVisitor(SpillMode.Restore);
        List<ASMInstr> abstract_asm = getTestNotAllLiveInstrs();
        List<ASMInstr> colored = visitor.allocate(abstract_asm);
        assertEquals(countTemps(colored), 0);
    }

    //test precolored nodes
    @Test
    public void testColoringPrecoloredReserve() {
        RegAllocationColoringVisitor visitor = new RegAllocationColoringVisitor(SpillMode.Reserve);
        List<ASMInstr> abstract_asm = getTestInstrsPrecolored(8);
        List<ASMInstr> colored = visitor.allocate(abstract_asm);
        assertEquals(countTemps(colored), 0);
    }

    @Test
    public void testColoringPrecoloredRestore() {
        RegAllocationColoringVisitor visitor = new RegAllocationColoringVisitor(SpillMode.Restore);
        List<ASMInstr> abstract_asm = getTestInstrsPrecolored(11);
        List<ASMInstr> colored = visitor.allocate(abstract_asm);
        assertEquals(countTemps(colored), 0);
    }

    @Test
    public void testColoringPrecoloredSpillReserve() {
        RegAllocationColoringVisitor visitor = new RegAllocationColoringVisitor(SpillMode.Reserve);
        List<ASMInstr> abstract_asm = getTestInstrsPrecolored(9);
        List<ASMInstr> colored = visitor.allocate(abstract_asm);
        assertNotEquals(countTemps(colored), 0);
    }

    @Test
    public void testColoringPrecoloredSpillRestore() {
        RegAllocationColoringVisitor visitor = new RegAllocationColoringVisitor(SpillMode.Restore);
        List<ASMInstr> abstract_asm = getTestInstrsPrecolored(12);
        List<ASMInstr> colored = visitor.allocate(abstract_asm);
        assertNotEquals(countTemps(colored), 0);
    }
}
