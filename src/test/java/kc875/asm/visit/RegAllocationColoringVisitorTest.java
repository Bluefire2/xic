package kc875.asm.visit;

import kc875.asm.visit.RegAllocationColoringVisitor.SpillMode;
import kc875.asm.*;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import java.util.*;

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
    returns instructions with n variables concurrently live
    format of instructions:
    MOV r9 1
    MOV r10 1
    MOV r11 1
    MOV tn 1, for all n
    MOV r9 t0
    MOV r10 t0
    MOV r11 t0
    */
    private List<ASMInstr> getTestInstrsPrecolored(int n) {
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
                    new ASMExprConst(1))
            );
        }
        for (String r : precolored) {
            instrs.add(new ASMInstr_2Arg(
                    ASMOpCode.ADD,
                    new ASMExprReg(r),
                    new ASMExprTemp(String.format("_temp_%d", 0))
            ));
        }
        return instrs;
    }

    /*
    returns instructions that test coalescing
    format of instructions:
    MOV tn 1 for n = 0...8
    ADD t0 tn for n = 1...8
    MOV tn 1 for n = 9...16
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
                    new ASMExprConst(1))
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

        for (int i = 2; i < 15; i++) {
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
        for (int i = 1; i < 15; i++) {
            tname = String.format("_temp_%d", i);
            instrs.add(new ASMInstr_2Arg(
                    ASMOpCode.MOV,
                    new ASMExprTemp(tname),
                    new ASMExprTemp(String.format("_temp_%d", 0))
            ));

        }
        for (int j = 1; j < 15; j++) {
            tname = String.format("_temp_%d", j);
            instrs.add(new ASMInstr_2Arg(
                    ASMOpCode.ADD,
                    new ASMExprTemp(String.format("_temp_%d", 0)),
                    new ASMExprTemp(tname)
            ));
        }
        return instrs;
    }

    //TODO 3 - test coloring with precolored nodes

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

    @Test
    public void testBuildInvariantsReserve() {
        RegAllocationColoringVisitor visitor = new RegAllocationColoringVisitor();
        List<ASMInstr> abstract_asm = getTestInstrs(8);
        visitor.buildInterferenceGraph(abstract_asm);
        assertTrue(visitor.checkDegreeInv());
        assertTrue(visitor.checkSimplifyWorklistInv());
        assertTrue(visitor.checkfreezeWorklistInv());
    }

    @Test
    public void testBuildInvariantsRestore() {
        RegAllocationColoringVisitor visitor = new RegAllocationColoringVisitor(SpillMode.Restore);
        List<ASMInstr> abstract_asm = getTestInstrs(8);
        visitor.buildInterferenceGraph(abstract_asm);
        assertTrue(visitor.checkDegreeInv());
        assertTrue(visitor.checkSimplifyWorklistInv());
        assertTrue(visitor.checkfreezeWorklistInv());
    }

    //test coloring for simple program with few live variables at a time
    @Test
    public void testColoringSimpleReserve() {
        //should successfully color 8 live vars
        RegAllocationColoringVisitor visitor = new RegAllocationColoringVisitor();
        List<ASMInstr> abstract_asm = getTestInstrs(8);
        List<ASMInstr> colored = visitor.allocate(abstract_asm);
        assertEquals(countTemps(colored), 0);
    }

    //test coloring for simple program that requires spilling
    //TODO spilling too many temps
    @Test
    public void testColoringSimpleSpillReserve() {
        //should fail to color 12 live vars - 3 unique temps spilled
        RegAllocationColoringVisitor visitor = new RegAllocationColoringVisitor();
        List<ASMInstr> abstract_asm = getTestInstrs(12);
        List<ASMInstr> colored = visitor.allocate(abstract_asm);
        assertNotEquals(countTemps(colored), 0);
    }

    @Test
    public void testColoringSimpleRestore() {
        //should successfully color 10 live vars
        RegAllocationColoringVisitor visitor = new RegAllocationColoringVisitor(SpillMode.Restore);
        List<ASMInstr> abstract_asm = getTestInstrs(10);
        List<ASMInstr> colored = visitor.allocate(abstract_asm);
        assertEquals(countTemps(colored), 0);
    }

    @Test
    public void testColoringSimpleSpillRestore() {
        //should fail to color 15 live vars
        RegAllocationColoringVisitor visitor = new RegAllocationColoringVisitor(SpillMode.Restore);
        List<ASMInstr> abstract_asm = getTestInstrs(15);
        List<ASMInstr> colored = visitor.allocate(abstract_asm);
        assertNotEquals(countTemps(colored), 0);
    }

    //lots of copies that can be coalesced
    @Test
    public void testColoringCoalesceReserve() {
        //should successfully color 8 live vars
        RegAllocationColoringVisitor visitor = new RegAllocationColoringVisitor();
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
        //should successfully color 8 live vars
        RegAllocationColoringVisitor visitor = new RegAllocationColoringVisitor();
        List<ASMInstr> abstract_asm = getTestDoNotCoalesceInstructions();
        for (ASMInstr i : abstract_asm){
            System.out.println(i.toString());
        }
        List<ASMInstr> colored = visitor.allocate(abstract_asm);
        assertNotEquals(countTemps(colored), 0);
    }

    @Test
    public void testColoringNotCoalesceRestore() {
        //should successfully color 10 live vars
        RegAllocationColoringVisitor visitor = new RegAllocationColoringVisitor(SpillMode.Restore);
        List<ASMInstr> abstract_asm = getTestDoNotCoalesceInstructions();
        List<ASMInstr> colored = visitor.allocate(abstract_asm);
        assertNotEquals(countTemps(colored), 0);
    }

    //test coloring for program where not all regs are live at the same time (register reuse)
    @Test
    public void testColoringReuseReserve() {
        RegAllocationColoringVisitor visitor = new RegAllocationColoringVisitor();
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

    //TODO NPE for precolored
    //test precolored nodes
    @Test
    public void testColoringPrecoloredReserve() {
        RegAllocationColoringVisitor visitor = new RegAllocationColoringVisitor();
        List<ASMInstr> abstract_asm = getTestInstrsPrecolored(6);
        List<ASMInstr> colored = visitor.allocate(abstract_asm);
        assertEquals(countTemps(colored), 0);
    }

    @Test
    public void testColoringPrecoloredRestore() {
        RegAllocationColoringVisitor visitor = new RegAllocationColoringVisitor(SpillMode.Restore);
        List<ASMInstr> abstract_asm = getTestInstrsPrecolored(9);
        List<ASMInstr> colored = visitor.allocate(abstract_asm);
        assertEquals(countTemps(colored), 0);
    }

    @Test
    public void testColoringPrecoloredSpillReserve() {
        RegAllocationColoringVisitor visitor = new RegAllocationColoringVisitor();
        List<ASMInstr> abstract_asm = getTestInstrsPrecolored(7);
        List<ASMInstr> colored = visitor.allocate(abstract_asm);
        assertNotEquals(countTemps(colored), 0);
    }

    @Test
    public void testColoringPrecoloredSpillRestore() {
        RegAllocationColoringVisitor visitor = new RegAllocationColoringVisitor(SpillMode.Restore);
        List<ASMInstr> abstract_asm = getTestInstrsPrecolored(10);
        List<ASMInstr> colored = visitor.allocate(abstract_asm);
        assertNotEquals(countTemps(colored), 0);
    }
}
