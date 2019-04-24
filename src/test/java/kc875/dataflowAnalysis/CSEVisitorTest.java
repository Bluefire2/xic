package kc875.dataflowAnalysis;

import edu.cornell.cs.cs4120.xic.ir.IRCompUnit;
import edu.cornell.cs.cs4120.xic.ir.visit.CommonSubexprElimVisitor;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.LinkedHashMap;

import static org.junit.Assert.*;

public class CSEVisitorTest {

    private CommonSubexprElimVisitor cseVisitor;

    @Before
    public void setUp() {
        cseVisitor = new CommonSubexprElimVisitor();
    }

    @After
    public void tearDown() {
    }

    @Test
    public void sanityCheck() {
        cseVisitor.removeCommonSubExpressions(new IRCompUnit("name", new LinkedHashMap<>()));
        assertTrue(true);
    }
}
