package kc875.ir.visit;

import edu.cornell.cs.cs4120.xic.ir.*;
import edu.cornell.cs.cs4120.xic.ir.visit.CommonSubexprElimVisitor;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import java.util.LinkedHashMap;

import static org.junit.Assert.assertTrue;

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
