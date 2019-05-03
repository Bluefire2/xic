package edu.cornell.cs.cs4120.xic.ir.visit;

import edu.cornell.cs.cs4120.xic.ir.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ConstantFoldVisitorTest {
    private ConstantFoldVisitor visitor;

    @Test
    public void simpleTest() {
        IRBinOp binOp = new IRBinOp(
                IRBinOp.OpType.ADD,
                new IRConst(5),
                new IRConst(6)
        );
        visitor.visit(binOp);
        // just make sure it doesn't fail or overflow the stack
    }

    @Test
    public void binOpSimpleTest() {
        IRBinOp binOp = new IRBinOp(
                IRBinOp.OpType.ADD,
                new IRConst(5),
                new IRConst(6)
        );
        IRNode folded = visitor.visit(binOp);
        assertEquals(folded, new IRConst(11));
    }

    @Test
    public void nestedBinOpTest() {
        IRBinOp binOp = new IRBinOp(
                IRBinOp.OpType.ADD,
                new IRBinOp(
                        IRBinOp.OpType.SUB,
                        new IRConst(5),
                        new IRConst(6)
                ),
                new IRBinOp(
                        IRBinOp.OpType.MUL,
                        new IRConst(10),
                        new IRConst(4)
                )
        );
        IRNode folded = visitor.visit(binOp);
        assertEquals(folded, new IRConst(39));
    }

    @Test
    public void cjumpTrueTest() {
        IRCJump cjump = new IRCJump(new IRConst(1), "t");
        IRJump folded = new IRJump(new IRName("t"));
        assertEquals(folded, visitor.fold(cjump));
    }

    @Test
    public void cjumpFalseTest() {
        IRCJump cjump = new IRCJump(new IRConst(0), "t", "f");
        IRJump folded = new IRJump(new IRName("f"));
        assertEquals(folded, visitor.fold(cjump));
    }

    @Test
    public void cjumpFalseNoLabelTest() {
        IRCJump cjump = new IRCJump(new IRConst(0), "t");
        IRSeq folded = new IRSeq();
        assertEquals(folded, visitor.fold(cjump));
    }

    @Before
    public void setUp() throws Exception {
        visitor = new ConstantFoldVisitor(new IRNodeFactory_c());
    }

    @After
    public void tearDown() throws Exception {
    }
}