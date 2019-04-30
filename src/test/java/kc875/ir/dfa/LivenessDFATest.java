package kc875.ir.dfa;
import edu.cornell.cs.cs4120.xic.ir.IRFuncDecl;
import edu.cornell.cs.cs4120.xic.ir.IRSeq;
import edu.cornell.cs.cs4120.xic.ir.dfa.IRGraph;
import edu.cornell.cs.cs4120.xic.ir.dfa.LivenessDFA;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class LivenessDFATest {

    private LivenessDFA livenessDFA;
    private IRGraph graph;

    @Before
    public void setUp() {
        graph = new IRGraph(new IRFuncDecl("", new IRSeq()));
        livenessDFA = new LivenessDFA(graph);
    }

    @After
    public void tearDown() {
    }
}
