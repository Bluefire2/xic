package edu.cornell.cs.cs4120.xic.ir.visit;

import edu.cornell.cs.cs4120.xic.ir.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class LoweringVisitorTest {
    private LoweringVisitor visitor;

    @Test
    public void testSimple() {
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
        assertEquals(flattened, visitor.lower(ns));
    }

    @Before
    public void setUp() throws Exception {
        visitor = new LoweringVisitor(new IRNodeFactory_c());
    }

    @After
    public void tearDown() throws Exception {
    }
}