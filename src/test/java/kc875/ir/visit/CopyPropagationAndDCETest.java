package kc875.ir.visit;

import edu.cornell.cs.cs4120.xic.ir.IRCompUnit;
import edu.cornell.cs.cs4120.xic.ir.visit.CopyPropagationVisitor;
import edu.cornell.cs.cs4120.xic.ir.visit.DeadCodeElimVisitor;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.LinkedHashMap;

import static org.junit.Assert.assertTrue;

public class CopyPropagationAndDCETest {

    private CopyPropagationVisitor copyPropagationVisitor;
    private DeadCodeElimVisitor deadCodeElimVisitor;

    @Before
    public void setUp() {
        copyPropagationVisitor = new CopyPropagationVisitor();
        deadCodeElimVisitor = new DeadCodeElimVisitor();
    }

    @After
    public void tearDown() {
    }

    @Test
    public void sanityCheck() {
        copyPropagationVisitor.propagateCopies(new IRCompUnit("name", new LinkedHashMap<>()));
        deadCodeElimVisitor.removeDeadCode(new IRCompUnit("name", new LinkedHashMap<>()));
        assertTrue(true);
    }

    @Test
    public void cpEndToEnd() {

    }

    @Test
    public void dceEndToEnd() {

    }

    @Test
    public void cpWithDce() {

    }
}
