package kc875.asm.visit;

import kc875.asm.*;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import static org.junit.Assert.assertEquals;

public class RegAllocationNaiveVisitorTest {
    private RegAllocationNaiveVisitor visitor =
            new RegAllocationNaiveVisitor(false);

    private int randInRange(int min, int max) {
        return min + (int) (Math.random() * ((max - min) + 1));
    }

    private static <T> T getRandom(T[] array) {
        int rnd = new Random().nextInt(array.length);
        return array[rnd];
    }

    @Test
    public void removeRepetitiveRSPInFuncSimpleTest() {
        ASMExprReg rsp = new ASMExprReg("rsp");
        List<ASMInstr> instrs = new ArrayList<>();
        instrs.add(new ASMInstrLabel("aaa"));
        instrs.add(new ASMInstr_2Arg(
                ASMOpCode.ENTER, new ASMExprConst(0), new ASMExprConst(0)
        ));
        instrs.add(new ASMInstr_2Arg(ASMOpCode.SUB, rsp, new ASMExprConst(8)));
        instrs.add(new ASMInstr_2Arg(ASMOpCode.SUB, rsp, new ASMExprConst(8)));
        instrs.add(new ASMInstr_0Arg(ASMOpCode.LEAVE));
        instrs.add(new ASMInstr_0Arg(ASMOpCode.RET));

        List<ASMInstr> transformed = visitor.removeRepetitiveRSPInFunc(instrs);
        ASMInstr_2Arg first = (ASMInstr_2Arg) transformed.get(1);
        assertEquals(first.getOpCode(), ASMOpCode.ENTER);
        assertEquals(first.getDest(), new ASMExprConst(16));
    }

    @Test
    public void removeRepetitiveRSPInFuncComplexTest() {
        ASMExprReg rsp = new ASMExprReg("rsp");
        List<ASMInstr> func = new ArrayList<>();
        func.add(new ASMInstrLabel("aaa"));
        func.add(new ASMInstr_2Arg(
                ASMOpCode.ENTER, new ASMExprConst(0), new ASMExprConst(0)
        ));

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
                ASMOpCode.IMUL,
                ASMOpCode.IDIV,
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
        func.addAll(instrs); // make sure label is first
        func.add(new ASMInstr_0Arg(ASMOpCode.LEAVE));
        func.add(new ASMInstr_0Arg(ASMOpCode.RET));

        List<ASMInstr> transformed = visitor.removeRepetitiveRSPInFunc(func);
        ASMInstr_2Arg first = (ASMInstr_2Arg) transformed.get(1);
        assertEquals(first.getOpCode(), ASMOpCode.ENTER);
        assertEquals(first.getDest(), new ASMExprConst(total));
    }

    @Test
    public void saveCalleeRegsInFuncTest() {
        ASMExprReg rsp = new ASMExprReg("rsp");
        List<ASMInstr> func = new ArrayList<>();
        func.add(new ASMInstrLabel("aaa"));
        func.add(new ASMInstr_2Arg(
                ASMOpCode.ENTER, new ASMExprConst(0), new ASMExprConst(0)
        ));

        List<ASMInstr> instrs = new ArrayList<>();
        // random instructions
        ASMExprReg rax = new ASMExprReg("rax");
        ASMExprReg rbx = new ASMExprReg("rbx");
        ASMExprReg rcx = new ASMExprReg("rcx");
        ASMExprReg rdx = new ASMExprReg("rdx");
        ASMExprReg rbp = new ASMExprReg("rbp");
        ASMExprReg r12 = new ASMExprReg("r12");
        ASMExprReg r13 = new ASMExprReg("r13");
        ASMExprReg r14 = new ASMExprReg("r14");
        ASMExprReg r15 = new ASMExprReg("r15");
        ASMExprReg[] regs = new ASMExprReg[] {rax, rbx, rcx, rdx, r12, r13, r14, r15};

        ASMOpCode[] ops = new ASMOpCode[] {
                ASMOpCode.ADD,
                ASMOpCode.SUB,
                ASMOpCode.IMUL,
                ASMOpCode.IDIV,
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
        instrs.add(new ASMInstr_2Arg(
                ASMOpCode.CMP, r12, new ASMExprMem(new ASMExprBinOpAdd(
                        rbp, new ASMExprConst(-8)
        ))));
        instrs.add(new ASMInstr_2Arg(
                ASMOpCode.TEST, r15, new ASMExprMem(new ASMExprBinOpAdd(
                rbp, new ASMExprConst(-16)
        ))));

        Collections.shuffle(instrs);
        func.addAll(instrs); // make sure label is first
        func.add(new ASMInstr_0Arg(ASMOpCode.LEAVE));
        func.add(new ASMInstr_0Arg(ASMOpCode.RET));

        List<ASMInstr> transformed = visitor.saveAllCalleeRegsInFunc(func);

        // test the start
        ASMInstr_2Arg first = (ASMInstr_2Arg) transformed.get(1);
        assertEquals(first.getOpCode(), ASMOpCode.ENTER);
        assertEquals(first.getDest(), new ASMExprConst(0));

        // pushed in a sorted order
        ASMInstr_1Arg second = (ASMInstr_1Arg) transformed.get(2);
        assertEquals(second.getOpCode(), ASMOpCode.PUSH);
        assertEquals(second.getArg(), r12);

        ASMInstr_1Arg third = (ASMInstr_1Arg) transformed.get(3);
        assertEquals(third.getOpCode(), ASMOpCode.PUSH);
        assertEquals(third.getArg(), r13);

        ASMInstr_1Arg fourth = (ASMInstr_1Arg) transformed.get(4);
        assertEquals(fourth.getOpCode(), ASMOpCode.PUSH);
        assertEquals(fourth.getArg(), r14);

        ASMInstr_1Arg fifth = (ASMInstr_1Arg) transformed.get(5);
        assertEquals(fifth.getOpCode(), ASMOpCode.PUSH);
        assertEquals(fifth.getArg(), r15);

        // no updates are performed to [rbp - k_t], ignore the below code
//        // test the updates to [rbp - k_t] by the pushing
//        for (ASMInstr ins : transformed) {
//            if (ins instanceof ASMInstr_2Arg) {
//                if (visitor.exprIsMemRBPMinusConst(((ASMInstr_2Arg) ins).getSrc())
//                        && ins.getOpCode() == ASMOpCode.CMP) {
//                    // constant must be -32 as three registers were pushed
//                    ASMInstr_2Arg ins2 = (ASMInstr_2Arg) ins;
//                    long v = ((ASMExprConst)
//                            ((ASMExprBinOpAdd)
//                                    ((ASMExprMem) ins2.getSrc()).getAddr())
//                                    .getRight())
//                            .getVal();
//                    assertEquals(-32, v);
//                } else if (visitor.exprIsMemRBPMinusConst(((ASMInstr_2Arg) ins).getSrc())
//                        && ins.getOpCode() == ASMOpCode.TEST) {
//                    // constant must be -40 as three registers were pushed
//                    ASMInstr_2Arg ins2 = (ASMInstr_2Arg) ins;
//                    long v = ((ASMExprConst)
//                            ((ASMExprBinOpAdd)
//                                    ((ASMExprMem) ins2.getSrc()).getAddr())
//                                    .getRight())
//                            .getVal();
//                    assertEquals(-40, v);
//                }
//            }
//        }

        // test the end
        int sz = transformed.size();
        // get last 4 instructions
        List<ASMInstr> tail = transformed.subList(sz-6, sz);
        ASMInstr_1Arg firstpop = (ASMInstr_1Arg) tail.get(0);
        assertEquals(firstpop.getOpCode(), ASMOpCode.POP);
        assertEquals(firstpop.getArg(), r15);

        ASMInstr_1Arg secondpop = (ASMInstr_1Arg) tail.get(1);
        assertEquals(secondpop.getOpCode(), ASMOpCode.POP);
        assertEquals(secondpop.getArg(), r14);

        ASMInstr_1Arg thirdpop = (ASMInstr_1Arg) tail.get(2);
        assertEquals(thirdpop.getOpCode(), ASMOpCode.POP);
        assertEquals(thirdpop.getArg(), r13);

        ASMInstr_1Arg fourthpop = (ASMInstr_1Arg) tail.get(3);
        assertEquals(fourthpop.getOpCode(), ASMOpCode.POP);
        assertEquals(fourthpop.getArg(), r12);
    }
}