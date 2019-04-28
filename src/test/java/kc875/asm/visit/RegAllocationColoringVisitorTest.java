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

    private int countTemps(List<ASMInstr> instrs) {
        int count = 0;
        for (ASMInstr i : instrs) {
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

    @Test
    public void testColoringSimpleReserve() {
        //should successfully color 8 live vars
        RegAllocationColoringVisitor visitor = new RegAllocationColoringVisitor();
        List<ASMInstr> abstract_asm = getTestInstrs(8);
        List<ASMInstr> colored = visitor.allocate(abstract_asm);
        assertEquals(countTemps(colored), 0);
    }

    @Test
    public void testFailedColoringSimpleReserve() {
        //should fail to color 12 live vars
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
    public void testFailedColoringSimpleRestore() {
        //should fail to color 15 live vars
        RegAllocationColoringVisitor visitor = new RegAllocationColoringVisitor(SpillMode.Restore);
        List<ASMInstr> abstract_asm = getTestInstrs(15);
        List<ASMInstr> colored = visitor.allocate(abstract_asm);
        assertNotEquals(countTemps(colored), 0);
    }
}
