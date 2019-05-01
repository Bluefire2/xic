package kc875.ir.dfa;

import com.google.common.collect.Lists;
import edu.cornell.cs.cs4120.xic.ir.*;
import edu.cornell.cs.cs4120.xic.ir.dfa.AvailableExprsDFA;
import edu.cornell.cs.cs4120.xic.ir.dfa.IRGraph;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

public class AvailableExprsDFATest {

    private AvailableExprsDFA availableExprsDFA;
    private IRGraph graph;

    @Before
    public void setUp() {
        graph = new IRGraph(new IRFuncDecl("", new IRSeq()));
        availableExprsDFA = new AvailableExprsDFA(graph);
    }

    @After
    public void tearDown() {
    }

    private boolean twoListsEqual(List a, List b) {
        return (a.size() == b.size() &&
                a.containsAll(b) && b.containsAll(a));
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

    @Test
    public void testPossibleAliasExprs() {
        IRExpr e = new IRTemp("e");
        List<IRExpr> exprs = Arrays.asList(
                new IRBinOp(IRBinOp.OpType.ADD, new IRMem(new IRConst(4)), new IRConst(0)),
                new IRMem(new IRTemp("y")),
                new IRBinOp(IRBinOp.OpType.ADD, new IRTemp("e"), new IRConst(1)),
                new IRBinOp(IRBinOp.OpType.SUB, new IRConst(7), new IRConst(8))
        );
        assert(twoListsEqual(exprs.subList(0, 2), AvailableExprsDFA.possibleAliasExprs(e, exprs).toList()));

    }

    @Test
    public void testExprsCanBeModified() {
        List<IRExpr> exprs = Arrays.asList(
                new IRBinOp(IRBinOp.OpType.ADD, new IRMem(new IRConst(4)), new IRConst(0)),
                new IRMem(new IRTemp("y")),
                new IRBinOp(IRBinOp.OpType.ADD, new IRTemp("e"), new IRConst(1)),
                new IRBinOp(IRBinOp.OpType.SUB, new IRConst(7), new IRConst(8))
        );
        assert(twoListsEqual(exprs.subList(0, 2), AvailableExprsDFA.exprsCanBeModified("f", exprs).toList()));
    }

    @Test
    public void testExprsGeneratedBy() {
        IRSeq seq = new IRSeq(new IRMove(new IRTemp("x"), new IRBinOp(IRBinOp.OpType.ADD, new IRConst(5), new IRConst(6))));
        IRFuncDecl func = new IRFuncDecl("f", seq);
        graph = new IRGraph(func);
        availableExprsDFA = new AvailableExprsDFA(graph);
        availableExprsDFA.runWorklistAlgo();
        List<IRExpr> expected = Lists.newArrayList(new IRTemp("x"), new IRBinOp(IRBinOp.OpType.ADD, new IRConst(5), new IRConst(6)), new IRConst(5), new IRConst(6));
        assert(twoListsEqual(Lists.newArrayList(availableExprsDFA.exprsGeneratedBy(graph.getStartNode())), expected));
    }
}

