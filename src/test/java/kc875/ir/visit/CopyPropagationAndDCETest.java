package kc875.ir.visit;

import edu.cornell.cs.cs4120.xic.ir.visit.CopyPropagationVisitor;
import edu.cornell.cs.cs4120.xic.ir.visit.DeadCodeElimVisitor;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

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
}
