package edu.cornell.cs.cs4120.xic.ir.dfa;

import com.google.common.collect.Lists;
import edu.cornell.cs.cs4120.xic.ir.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import static org.junit.Assert.assertEquals;


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

    @Test
    public void testExprsContainingTemp() {
        IRTemp temp = new IRTemp("temp");
        List<IRExpr> exprs = Arrays.asList(
                new IRBinOp(IRBinOp.OpType.ADD, temp, new IRConst(5)),
                new IRMem(new IRBinOp(IRBinOp.OpType.ADD,
                        new IRConst(6), temp)),
                new IRCall(new IRMem(temp)),
                new IRESeq(new IRSeq(), temp),
                new IRBinOp(IRBinOp.OpType.ADD,
                        new IRConst(9), new IRConst(8)),
                new IRMem(new IRTemp("other"))
        );
        assertEquals(
                AvailableExprsDFA.exprsContainingTemp(temp, new HashSet<>(exprs)),
                new HashSet<>(exprs.subList(0, 4))
        );
    }

    @Test
    public void testPossibleAliasExprs() {
        IRExpr e = new IRTemp("e");
        List<IRExpr> exprs = Arrays.asList(
                new IRBinOp(IRBinOp.OpType.ADD,
                        new IRMem(new IRConst(4)), new IRConst(0)),
                new IRMem(new IRTemp("y")),
                new IRBinOp(IRBinOp.OpType.ADD,
                        new IRTemp("e"), new IRConst(1)),
                new IRBinOp(IRBinOp.OpType.SUB,
                        new IRConst(7), new IRConst(8))
        );
        assertEquals(
                AvailableExprsDFA.possibleAliasExprs(e, new HashSet<>(exprs)),
                new HashSet<>(exprs.subList(0, 2))
        );
    }

    @Test
    public void testExprsCanBeModified() {
        List<IRExpr> exprs = Arrays.asList(
                new IRBinOp(IRBinOp.OpType.ADD,
                        new IRMem(new IRConst(4)), new IRConst(0)),
                new IRMem(new IRTemp("y")),
                new IRBinOp(IRBinOp.OpType.ADD,
                        new IRTemp("e"), new IRConst(1)),
                new IRBinOp(IRBinOp.OpType.SUB,
                        new IRConst(7), new IRConst(8))
        );
        assertEquals(
                AvailableExprsDFA.exprsCanBeModified("f", new HashSet<>(exprs)),
                new HashSet<>(exprs.subList(0, 2))
        );
    }

    @Test
    public void testExprsGeneratedBy() {
        IRSeq seq = new IRSeq(new IRMove(new IRTemp("x"),
                new IRBinOp(IRBinOp.OpType.ADD,
                        new IRConst(5), new IRConst(6))));
        IRFuncDecl func = new IRFuncDecl("f", seq);
        graph = new IRGraph(func);
        availableExprsDFA = new AvailableExprsDFA(graph);
        availableExprsDFA.runWorklistAlgo();
        List<IRExpr> expected = Lists.newArrayList(
                new IRBinOp(IRBinOp.OpType.ADD,
                        new IRConst(5),
                        new IRConst(6)),
                new IRConst(5), new IRConst(6));
        assertEquals(
                new HashSet<>(expected),
                availableExprsDFA.exprsGeneratedBy(graph.getStartNode()).getSet()
        );
    }
}

