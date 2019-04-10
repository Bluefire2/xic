package asm.visit;

import asm.*;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import static org.junit.Assert.*;

public class RegAllocationNaiveVisitorTest {
    private RegAllocationNaiveVisitor visitor = new RegAllocationNaiveVisitor();

    private int randInRange(int min, int max) {
        return min + (int) (Math.random() * ((max - min) + 1));
    }

    private static <T> T getRandom(T[] array) {
        int rnd = new Random().nextInt(array.length);
        return array[rnd];
    }

    @Test
    public void removeRepetitiveRSPInFuncSimple() {
        ASMExprReg rsp = new ASMExprReg("rsp");
        List<ASMInstr> instrs = new ArrayList<>();
        instrs.add(new ASMInstrLabel("aaa"));
        instrs.add(new ASMInstr_2Arg(ASMOpCode.SUB, rsp, new ASMExprConst(8)));
        instrs.add(new ASMInstr_2Arg(ASMOpCode.SUB, rsp, new ASMExprConst(8)));

        List<ASMInstr> transformed = visitor.removeRepetitiveRSPInFunc(instrs);
        ASMInstr_2Arg first = (ASMInstr_2Arg) transformed.get(1);
        assertEquals(first.getOpCode(), ASMOpCode.SUB);
        assertEquals(first.getDest(), rsp);
        assertEquals(first.getSrc(), new ASMExprConst(16));
    }

    @Test
    public void removeRepetitiveRSPInFuncComplex() {
        ASMExprReg rsp = new ASMExprReg("rsp");
        List<ASMInstr> func = new ArrayList<>();
        func.add(new ASMInstrLabel("aaa"));

        List<ASMInstr> instrs = new ArrayList<>();
        // random instructions
        ASMExprReg rax = new ASMExprReg("rax");
        ASMExprReg rbx = new ASMExprReg("rbx");
        ASMExprReg rcx = new ASMExprReg("rcx");
        ASMExprReg rdx = new ASMExprReg("rdx");
        ASMExprReg[] regs = new ASMExprReg[] {rax, rbx, rcx, rdx};

        ASMOpCode[] ops = new ASMOpCode[] {
                ASMOpCode.ADD,
                ASMOpCode.SUB,
                ASMOpCode.MUL,
                ASMOpCode.DIV,
                ASMOpCode.MOV,
                ASMOpCode.AND,
                ASMOpCode.OR
        };

        for (int i = 0; i < 1000; i++) {
            ASMExprReg reg1 = getRandom(regs);
            ASMExprReg reg2 = getRandom(regs);
            ASMOpCode op = getRandom(ops);
            instrs.add(new ASMInstr_2Arg(op, reg1, reg2));
        }

        int total = 0;
        for (int i = 0; i < 100; i++) {
            int curr = randInRange(0, 100);
            total += curr;
            instrs.add(new ASMInstr_2Arg(ASMOpCode.SUB, rsp, new ASMExprConst(curr)));
        }

        Collections.shuffle(instrs);
        func.addAll(instrs);
        List<ASMInstr> transformed = visitor.removeRepetitiveRSPInFunc(func);
        ASMInstr_2Arg first = (ASMInstr_2Arg) transformed.get(1);
        assertEquals(first.getOpCode(), ASMOpCode.SUB);
        assertEquals(first.getDest(), rsp);
        assertEquals(first.getSrc(), new ASMExprConst(total));
    }
}