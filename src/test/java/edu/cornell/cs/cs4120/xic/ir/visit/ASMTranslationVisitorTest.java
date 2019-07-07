package edu.cornell.cs.cs4120.xic.ir.visit;

import edu.cornell.cs.cs4120.xic.ir.*;
import edu.cornell.cs.cs4120.xic.ir.IRBinOp.OpType;
import kc875.asm.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import polyglot.util.Pair;

import java.util.ArrayList;
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
    public void testMoveTiling() {
        IRMove inc0 = new IRMove(new IRTemp("a"), new IRBinOp(OpType.ADD, new IRTemp("a"), new IRConst(1)));
        IRMove inc1 = new IRMove(new IRTemp("a"), new IRBinOp(OpType.ADD, new IRConst(1), new IRTemp("a")));
        IRMove inc2 = new IRMove(
                new IRMem(new IRTemp("a")),
                new IRBinOp(OpType.ADD, new IRMem(new IRTemp("a")), new IRConst(1)));
        IRMove dec0 = new IRMove(new IRTemp("a"), new IRBinOp(OpType.SUB, new IRTemp("a"), new IRConst(1)));
        IRMove dec1 = new IRMove(
                new IRMem(new IRTemp("a")),
                new IRBinOp(OpType.SUB, new IRMem(new IRTemp("a")), new IRConst(1)));

        IRMove single0 = new IRMove(new IRTemp("a"), new IRBinOp(OpType.ADD, new IRTemp("a"), new IRConst(2)));
        IRMove single1 = new IRMove(new IRTemp("a"), new IRBinOp(OpType.ADD, new IRConst(2), new IRTemp("a")));
        IRMove single2 = new IRMove(new IRTemp("a"), new IRBinOp(OpType.OR, new IRConst(2), new IRTemp("a")));
        IRMove single3 = new IRMove(new IRTemp("a"), new IRBinOp(OpType.AND, new IRTemp("a"), new IRConst(2)));
        IRMove single4 = new IRMove(
                new IRMem(new IRTemp("a")),
                new IRBinOp(OpType.SUB, new IRMem(new IRTemp("a")), new IRConst(2)));
        IRMove single5 = new IRMove(
                new IRMem(new IRTemp("a")),
                new IRBinOp(OpType.ADD, new IRMem(new IRTemp("a")), new IRTemp("b")));
        IRMove single6 = new IRMove(
                new IRMem(new IRTemp("a")),
                new IRBinOp(OpType.ADD, new IRTemp("b"), new IRMem(new IRTemp("a"))));

        IRMove multi0 = new IRMove(
                new IRMem(new IRTemp("a")),
                new IRBinOp(OpType.ADD, new IRTemp("b"), new IRConst(1)));
        IRMove multi1 = new IRMove(
                new IRTemp("a"),
                new IRBinOp(OpType.ADD, new IRTemp("b"), new IRConst(1)));
        IRMove multi2 = new IRMove(
                new IRMem(new IRTemp("a")),
                new IRBinOp(OpType.ADD, new IRMem(new IRTemp("a")), new IRMem(new IRTemp("b"))));

        IRMove simple0 = new IRMove(
                new IRTemp("_mir_t0"),
                new IRTemp("args"));

        List<ASMInstr> res0 = inc0.accept(visitor);
        List<ASMInstr> res1 = inc1.accept(visitor);
        List<ASMInstr> res2 = inc2.accept(visitor);
        List<ASMInstr> res3 = dec0.accept(visitor);
        List<ASMInstr> res4 = dec1.accept(visitor);
        List<ASMInstr> res5 = single0.accept(visitor);
        List<ASMInstr> res6 = single1.accept(visitor);
        List<ASMInstr> res7 = single2.accept(visitor);
        List<ASMInstr> res8 = single3.accept(visitor);
        List<ASMInstr> res9 = single4.accept(visitor);
        List<ASMInstr> res10 = single5.accept(visitor);
        List<ASMInstr> res11 = single6.accept(visitor);
        List<ASMInstr> res12 = multi0.accept(visitor);
        List<ASMInstr> res13 = multi1.accept(visitor);
        List<ASMInstr> res14 = multi2.accept(visitor);
        List<ASMInstr> res15 = simple0.accept(visitor);
        //check if instructions were generated
        assertEquals(res0.size(), 1);
        assertEquals(res1.size(), 1);
        assertEquals(res2.size(), 1);
        assertEquals(res0.get(0).getOpCode(), ASMOpCode.INC);
        assertEquals(res1.get(0).getOpCode(), ASMOpCode.INC);
        assertEquals(res2.get(0).getOpCode(), ASMOpCode.INC);
        assertEquals(res3.size(), 1);
        assertEquals(res4.size(), 1);
        assertEquals(res3.get(0).getOpCode(), ASMOpCode.DEC);
        assertEquals(res4.get(0).getOpCode(), ASMOpCode.DEC);
        assertEquals(res5.size(), 1);
        assertEquals(res6.size(), 1);
        assertEquals(res7.size(), 1);
        assertEquals(res8.size(), 1);
        assertEquals(res8.get(0).toString(), "    and a, 2");
        assertEquals(res9.size(), 1);
        assertEquals(res9.get(0).toString(), "    sub QWORD PTR [a], 2");
        assertEquals(res10.size(), 1);
        assertEquals(res11.size(), 1);
        assertNotEquals(res12.size(), 1);
        assertNotEquals(res13.size(), 1);
        assertNotEquals(res14.size(), 1);
        assertNotEquals(res12.size(), 0);
        assertNotEquals(res13.size(), 0);
        assertNotEquals(res14.size(), 0);
        assertEquals(res15.size(), 1);
        assertEquals(res15.get(0).toString(), "    mov _mir_t0, args");
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
        //  MOV _a, [c + d]
        //  MOV [a + b*4], _a
        IRExpr left = new IRMem(
                new IRBinOp(OpType.ADD, new IRTemp("a"), new IRBinOp(
                        OpType.MUL, new IRTemp("b"), new IRConst(4)
                ))
        );
        IRExpr right = new IRMem(
                new IRBinOp(OpType.ADD, new IRTemp("c"), new IRTemp("d"))
        );
        ASMExprTemp leftDestTemp = new ASMExprTemp("_a");
        ASMExprTemp rightDestTemp = new ASMExprTemp("_a");
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
        assertEquals(leftMem, dests.part1());
        assertEquals(rightDestTemp, dests.part2());
        assertEquals(1, instrs.size());
        assertEquals(
                new ASMInstr_2Arg(
                        ASMOpCode.MOV, rightDestTemp, rightMem
                ),
                instrs.get(0)
        );
    }

    @Before
    public void setUp() {
        visitor = new ASMTranslationVisitor();
    }

}
