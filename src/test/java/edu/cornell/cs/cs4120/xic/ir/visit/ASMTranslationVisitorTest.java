package edu.cornell.cs.cs4120.xic.ir.visit;

import asm.ASMExprMem;
import asm.ASMInstr;
import edu.cornell.cs.cs4120.xic.ir.*;
import edu.cornell.cs.cs4120.xic.ir.IRBinOp.OpType;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import polyglot.util.Pair;

import java.util.List;

import static org.junit.Assert.*;

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
    public void testTileMemExprMult() {
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
        Pair<List<ASMInstr>, ASMExprMem> res0 = visitor.tileMemExpr(new IRMem(noinstr0));
        Pair<List<ASMInstr>, ASMExprMem> res1 = visitor.tileMemExpr(new IRMem(noinstr1));
        Pair<List<ASMInstr>, ASMExprMem> res2 = visitor.tileMemExpr(new IRMem(noinstr2));
        Pair<List<ASMInstr>, ASMExprMem> res3 = visitor.tileMemExpr(new IRMem(instr0));
        Pair<List<ASMInstr>, ASMExprMem> res4 = visitor.tileMemExpr(new IRMem(instr1));
        Pair<List<ASMInstr>, ASMExprMem> res5 = visitor.tileMemExpr(new IRMem(instr2));
        Pair<List<ASMInstr>, ASMExprMem> res6 = visitor.tileMemExpr(new IRMem(instr3));
        Pair<List<ASMInstr>, ASMExprMem> res7 = visitor.tileMemExpr(new IRMem(instr4));
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


    @Before
    public void setUp() throws Exception {
        visitor = new ASMTranslationVisitor();
    }

    @After
    public void tearDown() throws Exception {
    }
}
