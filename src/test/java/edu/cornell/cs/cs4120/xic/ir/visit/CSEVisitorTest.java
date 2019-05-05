package edu.cornell.cs.cs4120.xic.ir.visit;

import edu.cornell.cs.cs4120.xic.ir.*;
import edu.cornell.cs.cs4120.xic.ir.dfa.IRGraph;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

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

    private boolean twoListsEqual(List a, List b) {
        return (a.size() == b.size() &&
                a.containsAll(b) && b.containsAll(a));
    }


    @Test
    public void sanityCheck() {
        cseVisitor.run(new IRCompUnit("name", new LinkedHashMap<>()));
        assertTrue(true);
    }

    @Test
    public void cseEndToEnd1() {
        HashMap<String, IRFuncDecl> funcMap = new LinkedHashMap<>();
        funcMap.put("f", new IRFuncDecl(
                "f",
                new IRSeq(
                        new IRMove(new IRTemp("x"),
                                new IRBinOp(IRBinOp.OpType.ADD,
                                        new IRTemp("y"),
                                        new IRConst(6))),
                        new IRMove(new IRTemp("z"),
                                new IRMem(new IRBinOp(IRBinOp.OpType.ADD,
                                        new IRTemp("y"),
                                        new IRConst(6)))),
                        new IRReturn(new IRBinOp(IRBinOp.OpType.ADD,
                                new IRTemp("y"),
                                new IRConst(6)))
                )));
        IRCompUnit compUnit = new IRCompUnit("compUnit", funcMap);
        IRCompUnit optimized = cseVisitor.run(compUnit);
        IRSeq expected =
                new IRSeq(
                        new IRMove(new IRTemp("_cse_t0"),
                                new IRBinOp(IRBinOp.OpType.ADD,
                                        new IRTemp("y"),
                                        new IRConst(6))),
                        new IRMove(new IRTemp("x"),
                                new IRTemp("_cse_t0")),
                        new IRMove(new IRTemp("z"),
                                new IRMem(new IRTemp("_cse_t0"))),
                        new IRReturn(new IRTemp("_cse_t0"))
                );
       assert (((IRSeq) optimized.functions().get("f").body()).stmts().size() == 4);
        try {
            IRGraph graph = new IRGraph(optimized.functions().get("f"));
            graph.show("CSETest1graph.dot");
        } catch (IOException e) {
        }
    }

    @Test
    public void cseEndToEnd2() {
        HashMap<String, IRFuncDecl> funcMap = new LinkedHashMap<>();
        funcMap.put("f", new IRFuncDecl(
                "f",
                new IRSeq(
                        new IRMove(new IRTemp("x"),
                                new IRBinOp(IRBinOp.OpType.ADD,
                                        new IRTemp("y"),
                                        new IRConst(6))),
                        new IRMove(new IRTemp("z"),
                                new IRMem(new IRBinOp(IRBinOp.OpType.ADD,
                                        new IRTemp("x"),
                                        new IRConst(5)))),
                        new IRMove(new IRTemp("a"),
                                new IRMem(new IRBinOp(IRBinOp.OpType.ADD,
                                        new IRTemp("x"),
                                        new IRConst(5)))),
                        new IRReturn(new IRBinOp(IRBinOp.OpType.ADD,
                                new IRTemp("y"),
                                new IRConst(6)))
                )));
        IRCompUnit compUnit = new IRCompUnit("compUnit", funcMap);
        IRCompUnit optimized = cseVisitor.run(compUnit);
        IRSeq expected =
                new IRSeq(
                        new IRMove(new IRTemp("_cse_t0"),
                                new IRBinOp(IRBinOp.OpType.ADD,
                                        new IRTemp("y"),
                                        new IRConst(6))),
                        new IRMove(new IRTemp("x"),
                                new IRTemp("_cse_t0")),
                        new IRMove(new IRTemp("_cse_t1"),
                                new IRMem(new IRBinOp(IRBinOp.OpType.ADD,
                                        new IRTemp("x"),
                                        new IRConst(5)))),
                        new IRMove(new IRTemp("z"),
                                new IRMem(new IRTemp("_cse_t1"))),
                        new IRMove(new IRTemp("a"),
                                new IRMem(new IRTemp("_cse_t1"))),
                        new IRReturn(new IRTemp("_cse_t0"))
                );
        assert (((IRSeq) optimized.functions().get("f").body()).stmts().size() == 6);
        try {
            IRGraph graph = new IRGraph(optimized.functions().get("f"));
            graph.show("CSETest2graph.dot");
        } catch (IOException e) {
        }
    }

}
