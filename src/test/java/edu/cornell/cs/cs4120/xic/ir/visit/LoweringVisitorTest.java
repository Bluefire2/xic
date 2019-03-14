package edu.cornell.cs.cs4120.xic.ir.visit;

import edu.cornell.cs.cs4120.xic.ir.*;
import edu.cornell.cs.cs4120.xic.ir.IRBinOp.OpType;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class LoweringVisitorTest {
    private LoweringVisitor visitor;

    private int tempcounter = 0;
    private String newTemp() {
        return String.format("_lir_t%d", (tempcounter++));
    }

    @Test
    public void testSimple() {
        System.out.println(newTemp());
        IRStmt stmt = new IRExp(new IRConst(5));
        IRStmt stmt2 = new IRExp(new IRConst(10));
        IRNode node = new IRSeq(stmt, stmt2);
        IRNode visited = node.visitChildren(visitor);

        IRStmt stmtp = new IRExp(new IRConst(5));
        IRStmt stmt2p = new IRExp(new IRConst(10));
        IRNode nodep = new IRSeq(stmtp, stmt2p);
        assertEquals(node, nodep);
    }

    @Test
    public void testNestedSeq() {
        IRStmt stmt = new IRLabel("l1");
        IRStmt stmt2 = new IRLabel("l2");
        IRStmt stmt3 = new IRLabel("l3");
        IRSeq s = new IRSeq(stmt, stmt2);
        IRSeq ns = new IRSeq(s, stmt3);
        IRSeq flattened = new IRSeq(stmt, stmt2, stmt3);
        assertEquals(flattened, visitor.visit(ns));
    }

    @Test
    public void testExp() {
        IRExp exp = new IRExp(new IRESeq(new IRReturn(), new IRConst(2)));
        IRReturn ret = new IRReturn();
        assertEquals(ret, visitor.lower(exp));
    }

    @Test
    public void testCall() {
        IRCall call = new IRCall(new IRName("f"),
                new IRESeq(new IRMove(new IRTemp("x"),
                        new IRTemp("y")),
                        new IRConst(1)),
                new IRConst(2));
        IRESeq lcall = new IRESeq(
                new IRSeq(new IRMove(new IRTemp("x"), new IRTemp("y")),
                         new IRMove(new IRTemp("_lir_t0"), new IRConst(1)),
                         new IRMove(new IRTemp("_lir_t1"), new IRConst(2)),
                         new IRMove(new IRTemp("_lir_t2"),
                                new IRCall(new IRName("f"),
                                        new IRTemp("_lir_t0"),
                                        new IRTemp("_lir_t1"))))
                        , new IRTemp("_lir_t2"));
        assertEquals(lcall, visitor.lower(call));
    }

    @Test
    public void manyNestedSeq() {
        IRSeq ret = new IRSeq(new IRMove(new IRTemp("x"), new IRTemp("y")),
                            new IRJump(new IRName("u")),
                            new IRLabel("j"),
                            new IRMove(new IRTemp("p"), new IRConst(7)));
        IRSeq nested = new IRSeq(new IRSeq(new IRMove(new IRTemp("x"), new IRTemp("y")),
                        new IRSeq(new IRJump(new IRName("u")),
                        new IRSeq(new IRLabel("j"),
                                new IRMove(new IRTemp("p"),
                                new IRConst(7))))));
        assertEquals(ret, visitor.lower(nested));
    }

    @Test
    public void testExprsDoNotCommute() {
        IRESeq e1 = new IRESeq(
                new IRJump(new IRTemp("a")),
                new IRConst(5)
        );
        IRESeq e2 = new IRESeq(
                new IRJump(new IRTemp("a")),
                new IRTemp("c")
        );
        assertFalse(visitor.ifExprsCommute(e1, e2));
    }

    @Test
    public void testExprsCommute() {
        IRESeq e1 = new IRESeq(
                new IRJump(new IRTemp("a")),
                new IRESeq(
                        new IRJump(new IRTemp("b")),
                        new IRESeq(
                                new IRJump(new IRTemp("c")),
                                new IRConst(10)
                        )
                )
        );
        IRESeq e2 = new IRESeq(
                new IRJump(new IRTemp("d")),
                new IRESeq(
                        new IRJump(new IRTemp("e")),
                        new IRESeq(
                                new IRJump(new IRTemp("f")),
                                new IRTemp("g")
                        )
                )
        );
        assertTrue(visitor.ifExprsCommute(e1, e2));
    }

    @Test
    public void testExprsDoNotCommuteMem() {
        IRESeq e1 = new IRESeq(
                new IRJump(new IRTemp("a")),
                new IRESeq(
                        new IRJump(new IRTemp("b")),
                        new IRESeq(
                                new IRJump(new IRTemp("c")),
                                new IRConst(10)
                        )
                )
        );
        IRESeq e2 = new IRESeq(
                new IRJump(new IRTemp("d")),
                new IRESeq(
                        new IRJump(new IRTemp("e")),
                        new IRESeq(
                                new IRMove(new IRTemp("f"), new IRConst(9)),
                                new IRMem(new IRESeq(
                                        new IRMove(new IRTemp("g"), new IRTemp("h")),
                                        new IRConst(8)
                                ))
                        )
                )
        );
        assertFalse(visitor.ifExprsCommute(e1, e2));
    }

    @Test
    public void testMoveCommute() {
        IRMove mov = new IRMove(new IRTemp("y"),
                new IRESeq(new IRLabel("l"),
                        new IRBinOp(IRBinOp.OpType.ADD,
                                new IRTemp("x"),
                                new IRConst(1))
                ));
        IRSeq lowered = new IRSeq(new IRLabel("l"),
                new IRMove(new IRTemp("y"), new IRBinOp(IRBinOp.OpType.ADD,
                        new IRTemp("x"),
                        new IRConst(1))));
        assertEquals(lowered, visitor.lower(mov));
    }

    @Test
    public void testMoveGeneral() {
        IRMove mov = new IRMove(new IRMem(new IRName("x")),
                new IRESeq(new IRLabel("l"),
                        new IRBinOp(IRBinOp.OpType.ADD,
                                new IRTemp("y"),
                                new IRConst(1))
                ));
        String t = newTemp();
        IRSeq lowered = new IRSeq(new IRMove(new IRTemp(t), new IRName("x")),
                        new IRLabel("l"),
                        new IRMove(new IRMem(new IRTemp(t)),
                                new IRBinOp(IRBinOp.OpType.ADD, new IRTemp("x"), new IRConst(1))));
        assertEquals(lowered, visitor.lower(mov));
    }

    @Test
    public void testMoveGeneralTwoMem() {
        IRMove mov = new IRMove(new IRMem(new IRName("y")),
                new IRESeq(new IRLabel("l"),new IRMem(new IRName("x"))));
        String t = newTemp();
        IRSeq lowered = new IRSeq(new IRMove(new IRTemp(t), new IRName("y")),
                new IRLabel("l"),
                new IRMove(new IRMem(new IRTemp(t)),
                        new IRMem(new IRName("x"))));
        assertEquals(lowered, visitor.lower(mov));
    }

    @Test
    public void testBinOpCommute() {
        IRESeq e1 = new IRESeq(
                new IRJump(new IRTemp("a")),
                new IRConst(5)
        );
        IRESeq e2 = new IRESeq(
                new IRJump(new IRTemp("b")),
                new IRTemp("c")
        );
        IRBinOp binOp = new IRBinOp(
                IRBinOp.OpType.ADD,
                e1,
                e2
        );
        IRESeq ret = new IRESeq(
                new IRSeq(
                        new IRJump(new IRTemp("a")),
                        new IRJump(new IRTemp("b"))
                ),
                new IRBinOp(
                        IRBinOp.OpType.ADD,
                        new IRConst(5),
                        new IRTemp("c")
                )
        );
        assertEquals(ret, visitor.lower(binOp));
    }

    @Test
    public void testBinopGeneral() {
        IRESeq e1 = new IRESeq(
                new IRJump(new IRTemp("a")),
                new IRConst(5)
        );
        IRESeq e2 = new IRESeq(
                new IRJump(new IRTemp("a")),
                new IRTemp("c")
        );
        IRBinOp binOp = new IRBinOp(
                IRBinOp.OpType.ADD,
                e1,
                e2
        );
        String t = newTemp();
        IRESeq ret = new IRESeq(
                new IRSeq(
                        new IRJump(new IRTemp("a")),
                        new IRMove(new IRTemp(t), new IRConst(5)),
                        new IRJump(new IRTemp("b"))
                ),
                new IRBinOp(
                        IRBinOp.OpType.ADD,
                        new IRTemp(t),
                        new IRTemp("c")
                )
        );
        assertEquals(ret, visitor.lower(binOp));
    }


    @Test
    public void testReordering0() {
        IRSeq original = new IRSeq(
                new IRCJump(new IRConst(0), "L2", "L3"),
                new IRLabel("L1"),
                new IRMove(new IRTemp("x"), new IRTemp("y")),
                new IRLabel("L2"),
                new IRMove(
                        new IRTemp("x"),
                        new IRBinOp(
                                OpType.ADD,
                                new IRTemp("y"),
                                new IRTemp("z")
                        )
                ),
                new IRJump(new IRName("L1")),
                new IRLabel("L3"),
                new IRExp(new IRCall(new IRName("f"), new IRTemp("x")))
        );
        CheckCanonicalIRVisitor cv = new CheckCanonicalIRVisitor();
        assertEquals(cv.visit(original), false);

        IRSeq reordered = visitor.reorderBasicBlocks(original);
        CheckCanonicalIRVisitor cv2 = new CheckCanonicalIRVisitor();
        assertEquals(cv2.visit(reordered), true);
    }

    @Before
    public void setUp() throws Exception {
        visitor = new LoweringVisitor(new IRNodeFactory_c());
    }

    @After
    public void tearDown() throws Exception {
    }
}