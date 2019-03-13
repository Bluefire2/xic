package edu.cornell.cs.cs4120.xic.ir.visit;

import edu.cornell.cs.cs4120.xic.ir.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class LoweringVisitorTest {
    private LoweringVisitor visitor;

    int tempcounter = 0;
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
    public void testMoveEseq() {
        IRMove movMir = new IRMove(new IRMem(new IRTemp("y")),
                            new IRESeq(new IRMove(new IRTemp("x"),
                                new IRTemp("y")),
                            new IRBinOp(IRBinOp.OpType.ADD,
                                    new IRTemp("x"),
                                    new IRConst(1))
                ));
        IRSeq lirMovSeq = new IRSeq(new IRMove(new IRTemp("x"),
                                                new IRTemp("y")),
                                    new IRMove(new IRMem(new IRTemp("y")),
                                        new IRBinOp(IRBinOp.OpType.ADD,
                                        new IRTemp("x"),
                                        new IRConst(1))));
        assertEquals(lirMovSeq, visitor.lower(movMir));
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
    public void testCJumps() {
        IRSeq cjmp = new IRSeq(new IRCJump(new IRBinOp(IRBinOp.OpType.AND, new IRConst(1), new IRTemp("x")),
                "t", "f"));
        IRSeq ret = new IRSeq(new IRCJump(new IRBinOp(IRBinOp.OpType.AND, new IRConst(1), new IRTemp("x")),
                "t"), new IRJump(new IRName("f")));
        assertEquals(ret, visitor.lower(cjmp));
    }

    @Test
    public void testBasicBlocks() {
        IRCJump cjmp = new IRCJump(new IRBinOp(IRBinOp.OpType.AND,
                new IRConst(1),
                new IRTemp("x")),
                "t", "f");
        IRSeq jmpseq = new IRSeq(cjmp, new IRLabel("f"), new IRMove(new IRTemp("x"), new IRTemp("y")));
        IRSeq ret = new IRSeq(new IRCJump(new IRBinOp(IRBinOp.OpType.AND,
                new IRConst(1),
                new IRTemp("x")),
                "t"), new IRMove(new IRTemp("x"), new IRTemp("y")));
        IRFuncDecl retfd = new IRFuncDecl("f", ret);
        IRFuncDecl fd = new IRFuncDecl("f", jmpseq);
        assertEquals(retfd, visitor.lower(fd));
    }

    //TODO:

    @Test
    public void testMoveCommute() {

    }

    @Test
    public void testMoveGeneral() {

    }

    @Test
    public void testBinOpCommute() {

    }

    @Test
    public void testBinopGeneral() {

    }


    @Before
    public void setUp() throws Exception {
        visitor = new LoweringVisitor(new IRNodeFactory_c());
    }

    @After
    public void tearDown() throws Exception {
    }
}