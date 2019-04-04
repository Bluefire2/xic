package edu.cornell.cs.cs4120.xic.ir.visit;

import asm.*;
import edu.cornell.cs.cs4120.xic.ir.*;
import edu.cornell.cs.cs4120.xic.ir.IRBinOp.OpType;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import polyglot.util.Pair;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class ASMTranslationVisitorTest {
    private ASMTranslationVisitor visitor;

    @Test
    public void testTileMemMultHelper() {
        //do not need instrs
        IRBinOp noinstr0 = new IRBinOp(OpType.MUL, new IRTemp("a"), new IRConst(2));
        IRBinOp noinstr1 = new IRBinOp(OpType.MUL, new IRConst(2), new IRTemp("a"));
        IRBinOp noinstr2 = new IRBinOp(OpType.MUL, new IRTemp("a"), new IRConst(4));
        //need instrs
        IRBinOp instr0 = new IRBinOp(OpType.MUL, new IRTemp("a"), new IRConst(5));
        IRBinOp instr1 = new IRBinOp(OpType.MUL, new IRTemp("a"), new IRTemp("b"));
        IRBinOp instr2 = new IRBinOp(OpType.MUL, new IRConst(2), new IRConst(4));
        IRBinOp instr3 = new IRBinOp(OpType.MUL,
                new IRBinOp(OpType.MUL, new IRTemp("a"), new IRConst(2)),
                new IRConst(4)
        );
        IRBinOp instr4 = new IRBinOp(OpType.MUL,
                new IRConst(2),
                new IRBinOp(OpType.MUL, new IRTemp("a"), new IRConst(2))
        );
        Pair<List<ASMInstr>, ASMExprMem> res0 = visitor.tileMemMult(noinstr0);
        Pair<List<ASMInstr>, ASMExprMem> res1 = visitor.tileMemMult(noinstr1);
        Pair<List<ASMInstr>, ASMExprMem> res2 = visitor.tileMemMult(noinstr2);
        Pair<List<ASMInstr>, ASMExprMem> res3 = visitor.tileMemMult(instr0);
        Pair<List<ASMInstr>, ASMExprMem> res4 = visitor.tileMemMult(instr1);
        Pair<List<ASMInstr>, ASMExprMem> res5 = visitor.tileMemMult(instr2);
        Pair<List<ASMInstr>, ASMExprMem> res6 = visitor.tileMemMult(instr3);
        Pair<List<ASMInstr>, ASMExprMem> res7 = visitor.tileMemMult(instr4);
        //check if instructions were generated
        assertEquals(res0.part1().size(), 0);
        assertEquals(res1.part1().size(), 0);
        assertEquals(res2.part1().size(), 0);
        assertNotEquals(res3.part1().size(), 0);
        assertNotEquals(res4.part1().size(), 0);
        assertNotEquals(res5.part1().size(), 0);
        assertNotEquals(res6.part1().size(), 0);
        assertNotEquals(res7.part1().size(), 0);
    }

    @Test
    public void testFlattenAdds() {
        IRBinOp test0 = new IRBinOp(OpType.ADD, new IRTemp("a"), new IRConst(2));
        IRBinOp test1 = new IRBinOp(OpType.ADD,
                new IRBinOp(OpType.ADD, new IRTemp("a"), new IRConst(2)),
                new IRConst(2));
        IRBinOp test2 = new IRBinOp(OpType.MUL,
                new IRBinOp(OpType.ADD, new IRTemp("a"), new IRConst(2)),
                new IRConst(2));
        IRBinOp test3 = new IRBinOp(OpType.MUL, new IRTemp("a"), new IRConst(2));
        IRBinOp test4 = new IRBinOp(OpType.ADD,
                new IRBinOp(OpType.ADD,
                        new IRBinOp(OpType.ADD, new IRConst(2), new IRConst(2)),
                        new IRConst(2)),
                new IRBinOp(OpType.ADD, new IRConst(2), new IRConst(2)));
        List<IRExpr> flattened0 = visitor.flattenAdds(test0);
        List<IRExpr> flattened1 = visitor.flattenAdds(test1);
        List<IRExpr> flattened2 = visitor.flattenAdds(test2);
        List<IRExpr> flattened3 = visitor.flattenAdds(test3);
        List<IRExpr> flattened4 = visitor.flattenAdds(test4);
        assertEquals(flattened0.size(), 2);
        assertEquals(flattened1.size(), 3);
        assertEquals(flattened2.size(), 1);
        assertEquals(flattened3.size(), 1);
        assertEquals(flattened4.size(), 5);
    }

    @Test
    public void testTileMemExpr() {
        //do not need instrs
        IRBinOp noinstr0 = new IRBinOp(OpType.MUL, new IRTemp("a"), new IRConst(2));
        IRBinOp noinstr1 = new IRBinOp(OpType.MUL, new IRConst(2), new IRTemp("a"));
        IRBinOp noinstr2 = new IRBinOp(OpType.MUL, new IRTemp("a"), new IRConst(4));
        IRBinOp noinstr3 = new IRBinOp(OpType.ADD, new IRTemp("a"), new IRConst(4));
        IRBinOp noinstr4 = new IRBinOp(OpType.ADD, new IRBinOp(OpType.MUL, new IRTemp("a"), new IRConst(4)), new IRConst(4));
        IRBinOp noinstr5 = new IRBinOp(OpType.ADD, new IRTemp("a"), new IRBinOp(OpType.ADD, new IRTemp("a"), new IRConst(4)));
        IRBinOp noinstr6 = new IRBinOp(OpType.ADD, new IRTemp("a"), new IRBinOp(OpType.ADD, new IRTemp("a"), new IRConst(-4)));
        IRBinOp noinstr7 = new IRBinOp(OpType.ADD, new IRTemp("a"), new IRConst(-8));
        //need instrs
        IRBinOp instr0 = new IRBinOp(OpType.MUL, new IRTemp("a"), new IRConst(5));
        IRBinOp instr1 = new IRBinOp(OpType.MUL, new IRTemp("a"), new IRTemp("b"));
        IRBinOp instr2 = new IRBinOp(OpType.MUL, new IRConst(2), new IRConst(4));
        IRBinOp instr3 = new IRBinOp(OpType.MUL,
                new IRBinOp(OpType.MUL, new IRTemp("a"), new IRConst(2)),
                new IRConst(4)
        );
        IRBinOp instr4 = new IRBinOp(OpType.MUL,
                new IRConst(2),
                new IRBinOp(OpType.MUL, new IRTemp("a"), new IRConst(2))
        );
        IRBinOp instr5 = new IRBinOp(OpType.ADD, new IRTemp("a"), new IRBinOp(OpType.ADD, new IRTemp("a"), new IRTemp("a")));
        Pair<List<ASMInstr>, ASMExprMem> res0 = visitor.tileMemExpr(new IRMem(noinstr0));
        Pair<List<ASMInstr>, ASMExprMem> res1 = visitor.tileMemExpr(new IRMem(noinstr1));
        Pair<List<ASMInstr>, ASMExprMem> res2 = visitor.tileMemExpr(new IRMem(noinstr2));
        Pair<List<ASMInstr>, ASMExprMem> res3 = visitor.tileMemExpr(new IRMem(noinstr3));
        Pair<List<ASMInstr>, ASMExprMem> res4 = visitor.tileMemExpr(new IRMem(noinstr4));
        Pair<List<ASMInstr>, ASMExprMem> res5 = visitor.tileMemExpr(new IRMem(noinstr5));
        Pair<List<ASMInstr>, ASMExprMem> res6 = visitor.tileMemExpr(new IRMem(noinstr6));
        Pair<List<ASMInstr>, ASMExprMem> res7 = visitor.tileMemExpr(new IRMem(noinstr7));
        Pair<List<ASMInstr>, ASMExprMem> res8 = visitor.tileMemExpr(new IRMem(instr0));
        Pair<List<ASMInstr>, ASMExprMem> res9 = visitor.tileMemExpr(new IRMem(instr1));
        Pair<List<ASMInstr>, ASMExprMem> res10 = visitor.tileMemExpr(new IRMem(instr2));
        Pair<List<ASMInstr>, ASMExprMem> res11 = visitor.tileMemExpr(new IRMem(instr3));
        Pair<List<ASMInstr>, ASMExprMem> res12 = visitor.tileMemExpr(new IRMem(instr4));
        Pair<List<ASMInstr>, ASMExprMem> res13 = visitor.tileMemExpr(new IRMem(instr5));
        //check if instructions were generated
        assertEquals(res0.part1().size(), 0);
        assertEquals(res1.part1().size(), 0);
        assertEquals(res2.part1().size(), 0);
        assertEquals(res3.part1().size(), 0);
        assertEquals(res4.part1().size(), 0);
        assertEquals(res5.part1().size(), 0);
        assertEquals(res6.part1().size(), 0);
        assertEquals(res7.part1().size(), 0);
        assertNotEquals(res8.part1().size(), 0);
        assertNotEquals(res9.part1().size(), 0);
        assertNotEquals(res10.part1().size(), 0);
        assertNotEquals(res11.part1().size(), 0);
        assertNotEquals(res12.part1().size(), 0);
        assertNotEquals(res13.part1().size(), 0);
    }

    @Test
    public void testASMExprOfBinOpTempTemp() {
        // left: "_a", right: "_b"
        // results:
        //  MOV a, _a
        //  a, _b
        IRExpr left = new IRTemp("_a");
        IRExpr right = new IRTemp("_b");
        ASMExprTemp leftDestTemp = new ASMExprTemp("a");
        ASMExprTemp rightDestTemp = new ASMExprTemp("");
        List<ASMInstr> instrs = new ArrayList<>();

        Pair<ASMExpr, ASMExpr> dests = visitor.asmExprOfBinOp(
                left, right, leftDestTemp, rightDestTemp, instrs
        );

        assertEquals(leftDestTemp, dests.part1());
        assertEquals(new ASMExprTemp("_b"), dests.part2());
        assertEquals(1, instrs.size());
        assertEquals(
                new ASMInstr_2Arg(
                        ASMOpCode.MOV, leftDestTemp, new ASMExprTemp("_a")
                ),
                instrs.get(0)
        );
    }

    @Test
    public void testASMExprOfBinOpConstConst() {
        // left: 3, right: 5
        // results:
        //  MOV a, 3
        //  a, 5
        IRExpr left = new IRConst(3);
        IRExpr right = new IRConst(5);
        ASMExprTemp leftDestTemp = new ASMExprTemp("a");
        ASMExprTemp rightDestTemp = new ASMExprTemp("");
        List<ASMInstr> instrs = new ArrayList<>();

        Pair<ASMExpr, ASMExpr> dests = visitor.asmExprOfBinOp(
                left, right, leftDestTemp, rightDestTemp, instrs
        );

        assertEquals(leftDestTemp, dests.part1());
        assertEquals(new ASMExprConst(5), dests.part2());
        assertEquals(1, instrs.size());
        assertEquals(
                new ASMInstr_2Arg(
                        ASMOpCode.MOV, leftDestTemp, new ASMExprConst(3)
                ),
                instrs.get(0)
        );
    }

    @Test
    public void testASMExprOfBinOpConstTemp() {
        // left: 3, right: "b"
        // results:
        //  MOV a, 3
        //  a, b
        IRExpr left = new IRConst(3);
        IRExpr right = new IRTemp("b");
        ASMExprTemp leftDestTemp = new ASMExprTemp("a");
        ASMExprTemp rightDestTemp = new ASMExprTemp("");
        List<ASMInstr> instrs = new ArrayList<>();

        Pair<ASMExpr, ASMExpr> dests = visitor.asmExprOfBinOp(
                left, right, leftDestTemp, rightDestTemp, instrs
        );

        assertEquals(leftDestTemp, dests.part1());
        assertEquals(new ASMExprTemp("b"), dests.part2());
        assertEquals(1, instrs.size());
        assertEquals(
                new ASMInstr_2Arg(
                        ASMOpCode.MOV, leftDestTemp, new ASMExprConst(3)
                ),
                instrs.get(0)
        );
    }

    @Test
    public void testASMExprOfBinOpTempConst() {
        // left: "_a", right: 5
        // results:
        //  MOV a, _a
        //  a, 5
        IRExpr left = new IRTemp("_a");
        IRExpr right = new IRConst(5);
        ASMExprTemp leftDestTemp = new ASMExprTemp("a");
        ASMExprTemp rightDestTemp = new ASMExprTemp("");
        List<ASMInstr> instrs = new ArrayList<>();

        Pair<ASMExpr, ASMExpr> dests = visitor.asmExprOfBinOp(
                left, right, leftDestTemp, rightDestTemp, instrs
        );

        assertEquals(leftDestTemp, dests.part1());
        assertEquals(new ASMExprConst(5), dests.part2());
        assertEquals(1, instrs.size());
        assertEquals(
                new ASMInstr_2Arg(
                        ASMOpCode.MOV, leftDestTemp, new ASMExprTemp("_a")
                ),
                instrs.get(0)
        );
    }

    @Test
    public void testASMExprOfBinOpBinOpTemp() {
        // left: 3 + "a" + 2, right: "b"
        // results:
        //  MOV _a, 3
        //  ADD _a, a
        //  ADD _a, 2
        //  a, b
        IRExpr left = new IRBinOp(
                OpType.ADD,
                new IRBinOp(OpType.ADD, new IRConst(3), new IRTemp("a")),
                new IRConst(2)
        );
        IRExpr right = new IRTemp("b");
        ASMExprTemp leftDestTemp = new ASMExprTemp("_a");
        ASMExprTemp rightDestTemp = new ASMExprTemp("");
        List<ASMInstr> instrs = new ArrayList<>();

        Pair<ASMExpr, ASMExpr> dests = visitor.asmExprOfBinOp(
                left, right, leftDestTemp, rightDestTemp, instrs
        );

        assertEquals(leftDestTemp, dests.part1());
        assertEquals(new ASMExprTemp("b"), dests.part2());
        assertEquals(3, instrs.size());
        assertEquals(
                new ASMInstr_2Arg(
                        ASMOpCode.MOV, leftDestTemp, new ASMExprConst(3)
                ),
                instrs.get(0)
        );
        assertEquals(
                new ASMInstr_2Arg(
                        ASMOpCode.ADD, leftDestTemp, new ASMExprTemp("a")
                ),
                instrs.get(1)
        );
        assertEquals(
                new ASMInstr_2Arg(
                        ASMOpCode.ADD, leftDestTemp, new ASMExprConst(2)
                ),
                instrs.get(2)
        );
    }

    @Test
    public void testASMExprOfBinOpTempBinOp() {
        // left: "a", right: 3 + "b" + 2
        // results:
        //  MOV _a, a
        //  MOV _b, 3
        //  ADD _b, a
        //  ADD _b, 2
        //  _a, _b
        IRExpr left = new IRTemp("a");
        IRExpr right = new IRBinOp(
                OpType.ADD,
                new IRBinOp(OpType.ADD, new IRConst(3), new IRTemp("b")),
                new IRConst(2)
        );
        ASMExprTemp leftDestTemp = new ASMExprTemp("_a");
        ASMExprTemp rightDestTemp = new ASMExprTemp("_b");
        List<ASMInstr> instrs = new ArrayList<>();

        Pair<ASMExpr, ASMExpr> dests = visitor.asmExprOfBinOp(
                left, right, leftDestTemp, rightDestTemp, instrs
        );

        assertEquals(leftDestTemp, dests.part1());
        assertEquals(rightDestTemp, dests.part2());
        assertEquals(4, instrs.size());
        assertEquals(
                new ASMInstr_2Arg(
                        ASMOpCode.MOV, leftDestTemp, new ASMExprTemp("a")
                ),
                instrs.get(0)
        );
        assertEquals(
                new ASMInstr_2Arg(
                        ASMOpCode.MOV, rightDestTemp, new ASMExprConst(3)
                ),
                instrs.get(1)
        );
        assertEquals(
                new ASMInstr_2Arg(
                        ASMOpCode.ADD, rightDestTemp, new ASMExprTemp("b")
                ),
                instrs.get(2)
        );
        assertEquals(
                new ASMInstr_2Arg(
                        ASMOpCode.ADD, rightDestTemp, new ASMExprConst(2)
                ),
                instrs.get(3)
        );
    }

    @Test
    public void testASMExprOfBinOpMemTemp() {
        // left: ["a" + "b"*4], right: "c"
        // results:
        //  [a + b*4], c
        IRExpr left = new IRMem(
                new IRBinOp(OpType.ADD, new IRTemp("a"), new IRBinOp(
                        OpType.MUL, new IRTemp("b"), new IRConst(4)
                ))
        );
        IRExpr right = new IRTemp("c");
        ASMExprTemp leftDestTemp = new ASMExprTemp("");
        ASMExprTemp rightDestTemp = new ASMExprTemp("");
        List<ASMInstr> instrs = new ArrayList<>();

        Pair<ASMExpr, ASMExpr> dests = visitor.asmExprOfBinOp(
                left, right, leftDestTemp, rightDestTemp, instrs
        );

        ASMExprMem leftMem = new ASMExprMem(
                new ASMExprBinOpAdd(
                        new ASMExprTemp("a"),
                        new ASMExprBinOpMult(
                                new ASMExprTemp("b"),
                                new ASMExprConst(4)
                        )
                )
        );

        assertEquals(leftMem, dests.part1());
        assertEquals(new ASMExprTemp("c"), dests.part2());
        assertEquals(0, instrs.size());
    }

    @Test
    public void testASMExprOfBinOpTempMem() {
        // left: "a", right: ["b" + "c"*4]
        // results:
        //  MOV _a, a
        //  _a, [b + c*4]
        IRExpr left = new IRTemp("a");
        IRExpr right = new IRMem(
                new IRBinOp(OpType.ADD, new IRTemp("b"), new IRBinOp(
                        OpType.MUL, new IRTemp("c"), new IRConst(4)
                ))
        );
        ASMExprTemp leftDestTemp = new ASMExprTemp("_a");
        ASMExprTemp rightDestTemp = new ASMExprTemp("");
        List<ASMInstr> instrs = new ArrayList<>();

        Pair<ASMExpr, ASMExpr> dests = visitor.asmExprOfBinOp(
                left, right, leftDestTemp, rightDestTemp, instrs
        );

        ASMExprMem rightMem = new ASMExprMem(
                new ASMExprBinOpAdd(
                        new ASMExprTemp("b"),
                        new ASMExprBinOpMult(
                                new ASMExprTemp("c"),
                                new ASMExprConst(4)
                        )
                )
        );

        assertEquals(new ASMExprTemp("_a"), dests.part1());
        assertEquals(rightMem, dests.part2());
        assertEquals(1, instrs.size());
        assertEquals(
                new ASMInstr_2Arg(
                        ASMOpCode.MOV, leftDestTemp, new ASMExprTemp("a")
                ),
                instrs.get(0)
        );
    }

    @Test
    public void testASMExprOfBinOpMemMem() {
        // left: ["a" + "b"*4], right: ["c" + "d"]
        // results:
        //  MOV _a, [a + b*4]
        //  _a, [c + d]
        IRExpr left = new IRMem(
                new IRBinOp(OpType.ADD, new IRTemp("a"), new IRBinOp(
                        OpType.MUL, new IRTemp("b"), new IRConst(4)
                ))
        );
        IRExpr right = new IRMem(
                new IRBinOp(OpType.ADD, new IRTemp("c"), new IRTemp("d"))
        );
        ASMExprTemp leftDestTemp = new ASMExprTemp("_a");
        ASMExprTemp rightDestTemp = new ASMExprTemp("");
        List<ASMInstr> instrs = new ArrayList<>();

        Pair<ASMExpr, ASMExpr> dests = visitor.asmExprOfBinOp(
                left, right, leftDestTemp, rightDestTemp, instrs
        );

        ASMExprMem leftMem = new ASMExprMem(
                new ASMExprBinOpAdd(
                        new ASMExprTemp("a"),
                        new ASMExprBinOpMult(
                                new ASMExprTemp("b"),
                                new ASMExprConst(4)
                        )
                )
        );
        // For some reason, c and d are swapped when the memTile is
        // created. Since + is commutative, it can be ignored however.
        ASMExprMem rightMem = new ASMExprMem(new ASMExprBinOpAdd(
                new ASMExprTemp("d"), new ASMExprTemp("c")
        ));
        assertEquals(leftDestTemp, dests.part1());
        assertEquals(rightMem, dests.part2());
        assertEquals(1, instrs.size());
        assertEquals(
                new ASMInstr_2Arg(
                        ASMOpCode.MOV, leftDestTemp, leftMem
                ),
                instrs.get(0)
        );
    }

    @Test
    public void testFunctionCall_Simple() {
        IRFuncDecl f = new IRFuncDecl("_If_iii", new IRSeq(
                new IRReturn(
                        new IRBinOp(OpType.ADD,
                                new IRTemp("x"),
                                new IRTemp("y")))));
        IRCall fcall = new IRCall(new IRName("_If_iii"),
                Arrays.asList(new IRConst(4), new IRConst(5)));
        List<ASMInstr> expected = Arrays.asList(
                new ASMInstr_2Arg(ASMOpCode.MOV, new ASMExprTemp("_asm_t0"), new ASMExprConst(4)),
                new ASMInstr_2Arg(ASMOpCode.MOV, new ASMExprReg("rdi"), new ASMExprTemp("_asm_t0")),
                new ASMInstr_2Arg(ASMOpCode.MOV, new ASMExprTemp("_asm_t1"), new ASMExprConst(5)),
                new ASMInstr_2Arg(ASMOpCode.MOV, new ASMExprReg("rsi"), new ASMExprTemp("_asm_t1")),
                new ASMInstr_1Arg(ASMOpCode.CALL, new ASMExprName("_If_iii")),
                new ASMInstr_2Arg(ASMOpCode.MOV, new ASMExprTemp("tmp"), new ASMExprReg("rax")),
                new ASMInstr_1Arg(ASMOpCode.PUSH, new ASMExprReg("rbp")),
                new ASMInstr_2Arg(ASMOpCode.MOV, new ASMExprReg("rbp"), new ASMExprReg("rsp")),
                new ASMInstr_2Arg(ASMOpCode.SUB, new ASMExprReg("rbp"), new ASMExprConst(16)),
                new ASMInstr_2Arg(ASMOpCode.MOV, new ASMExprTemp("_asm_t3"), new ASMExprTemp("x")),
                new ASMInstr_2Arg(ASMOpCode.ADD, new ASMExprTemp("_asm_t3"), new ASMExprTemp("y")),
                new ASMInstr_2Arg(ASMOpCode.MOV, new ASMExprReg("rax"), new ASMExprTemp("_asm_t3")),
                new ASMInstr_2Arg(ASMOpCode.ADD, new ASMExprReg("rbp"), new ASMExprConst(16)),
                new ASMInstr_2Arg(ASMOpCode.MOV, new ASMExprReg("rsp"), new ASMExprReg("rbp")),
                new ASMInstr_1Arg(ASMOpCode.POP, new ASMExprReg("rbp")),
                new ASMInstr_0Arg(ASMOpCode.RET)
        );
        List<ASMInstr> actual = visitor.visit(fcall, new ASMExprTemp("tmp"));
        List<ASMInstr> b = visitor.visit(f);
        actual.addAll(b);
        assertEquals(expected, actual);
    }

    @Before
    public void setUp() throws Exception {
        visitor = new ASMTranslationVisitor();
    }

    @After
    public void tearDown() throws Exception {
    }
}
