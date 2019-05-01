package kc875.ir.dfa;
import com.google.common.collect.Lists;
import edu.cornell.cs.cs4120.xic.ir.*;
import edu.cornell.cs.cs4120.xic.ir.dfa.IRGraph;
import edu.cornell.cs.cs4120.xic.ir.dfa.ReachingDefnsDFA;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ReachingDefnsDFATest {

    private ReachingDefnsDFA reachingDefnsDFA;
    private IRGraph graph;

    @Before
    public void setUp() {
        graph = new IRGraph(new IRFuncDecl("", new IRSeq()));
        reachingDefnsDFA = new ReachingDefnsDFA(graph);
    }

    @After
    public void tearDown() {
    }

    private boolean twoListsEqual(List a, List b) {
        return (a.size() == b.size() &&
                a.containsAll(b) && b.containsAll(a));
    }

    @Test
    public void testDefs() {
        IRFuncDecl fn = new IRFuncDecl(
                "f",
                new IRSeq(
                        new IRMove(new IRTemp("x"), new IRConst(7)),
                        new IRLabel("x"),
                        new IRMove(new IRTemp("y"), new IRTemp("x")),
                        new IRMove(new IRTemp("x"), new IRTemp("z")),
                        new IRReturn(new IRTemp("x"))
                ));

        IRGraph graph = new IRGraph(fn);
        Set<IRGraph.Node> xnodes = ReachingDefnsDFA.defs(new IRTemp("x"), graph);
        List<IRStmt> actual = new ArrayList<>();
        for (IRGraph.Node n : xnodes) {
            actual.add(graph.getStmt(n));
        }
        List<IRStmt> expected = Lists.newArrayList(new IRMove(new IRTemp("x"),
                new IRConst(7)),  new IRMove(new IRTemp("x"), new IRTemp("z")));
        assert(twoListsEqual(actual, expected));
    }
 }