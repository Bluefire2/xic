package edu.cornell.cs.cs4120.xic.ir.visit;

import edu.cornell.cs.cs4120.xic.ir.IRNode;
import edu.cornell.cs.cs4120.xic.ir.IRNodeFactory_c;
import edu.cornell.cs.cs4120.xic.ir.IRSeq;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class LoweringVisitorTest {
    private LoweringVisitor visitor;

    @Test
    public void testSimple() {
        IRNode node = new IRSeq();
        IRNode visited = node.visitChildren(visitor);
    }

    @Before
    public void setUp() throws Exception {
        visitor = new LoweringVisitor(new IRNodeFactory_c());
    }

    @After
    public void tearDown() throws Exception {
    }
}