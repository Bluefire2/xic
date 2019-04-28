package kc875.dataflowAnalysis;

import edu.cornell.cs.cs4120.xic.ir.*;
import edu.cornell.cs.cs4120.xic.ir.dfa.AvailableExprsDFA;
import edu.cornell.cs.cs4120.xic.ir.dfa.IRGraph;
import edu.cornell.cs.cs4120.xic.ir.visit.CommonSubexprElimVisitor;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;

import static org.junit.Assert.assertTrue;

public class CSEVisitorTest {

    private CommonSubexprElimVisitor cseVisitor;
    private AvailableExprsDFA availableExprsDFA;

    @Before
    public void setUp() {
        cseVisitor = new CommonSubexprElimVisitor();
        availableExprsDFA = new AvailableExprsDFA(new IRGraph(
                new IRFuncDecl("", new IRSeq())
        ));
    }

    @After
    public void tearDown() {
    }

    private boolean twoListsEqual(List a, List b) {
        return (a.size() == b.size() &&
                a.containsAll(b) && b.containsAll(a));
    }

    @Test
    public void sanityCheck() {
        cseVisitor.removeCommonSubExpressions(new IRCompUnit("name", new LinkedHashMap<>()));
        assertTrue(true);
    }


    @Test
    public void testExprsContainingTemp() {
        IRTemp temp = new IRTemp("temp");
        List<IRExpr> exprs = Arrays.asList(
                new IRBinOp(IRBinOp.OpType.ADD, temp, new IRConst(5)),
                new IRMem(new IRBinOp(IRBinOp.OpType.ADD, new IRConst(6), temp)),
                new IRCall(new IRMem(temp)),
                new IRESeq(new IRSeq(), temp),
                new IRBinOp(IRBinOp.OpType.ADD, new IRConst(9), new IRConst(8)),
                new IRMem(new IRTemp("other"))
                );
        assert(twoListsEqual(exprs.subList(0, 4), availableExprsDFA.exprsContainingTemp(temp, exprs).toList()));
    }
}
