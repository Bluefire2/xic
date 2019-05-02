package kc875.ir.visit;

import edu.cornell.cs.cs4120.xic.ir.*;
import edu.cornell.cs.cs4120.xic.ir.dfa.IRGraph;
import edu.cornell.cs.cs4120.xic.ir.visit.CopyPropagationVisitor;
import edu.cornell.cs.cs4120.xic.ir.visit.DeadCodeElimVisitor;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.HashMap;
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
        copyPropagationVisitor.propagateCopies(new IRCompUnit("name",
                new LinkedHashMap<>()));
        deadCodeElimVisitor.run(new IRCompUnit("name",
                new LinkedHashMap<>()));
        assertTrue(true);
    }

    @Test
    public void cpEndToEnd() {
        HashMap<String, IRFuncDecl> funcMap = new HashMap<>();
        funcMap.put("f",
                new IRFuncDecl("f",
                    new IRSeq(
                            new IRMove(
                                    new IRTemp("x"),
                                    new IRTemp("y")),
                            new IRMove(new IRTemp("z"),
                                        new IRBinOp(IRBinOp.OpType.MUL,
                                            new IRTemp("x"),
                                            new IRConst(2)))
                    )));
        IRCompUnit compUnit = new IRCompUnit("compUnit", funcMap);
        IRCompUnit optimized = copyPropagationVisitor.propagateCopies(compUnit);
        IRSeq expected = new IRSeq(
                new IRMove(new IRTemp("x"), new IRTemp("y")),
                new IRMove(new IRTemp("z"),
                            new IRBinOp(IRBinOp.OpType.MUL,
                                    new IRTemp("y"),
                                    new IRConst(2)))
        );
        assert(((IRSeq) optimized.functions().get("f").body()).stmts().size() == 2);
    }

    @Test
    public void dceEndToEnd() {
        HashMap<String, IRFuncDecl> funcMap = new HashMap<>();
        funcMap.put("f",
                new IRFuncDecl("f",
                        new IRSeq(
                                new IRLabel("l1"),
                                new IRMove(
                                        new IRTemp("x"),
                                        new IRTemp("y")),
                                new IRMove(new IRTemp("z"),
                                        new IRBinOp(IRBinOp.OpType.MUL,
                                                new IRTemp("a"),
                                                new IRConst(2))),
                                new IRMove(new IRTemp("n"), new IRConst(6)),
                                new IRReturn(new IRTemp("z"))
                        )));
        IRCompUnit compUnit = new IRCompUnit("compUnit", funcMap);
        IRCompUnit optimized = deadCodeElimVisitor.run(compUnit);
        IRSeq expected = new IRSeq(
                new IRLabel("l1"),
                new IRMove(new IRTemp("z"),
                        new IRBinOp(IRBinOp.OpType.MUL,
                                new IRTemp("a"),
                                new IRConst(2))),
                new IRReturn(new IRTemp("z"))
        );
        assert(((IRSeq) optimized.functions().get("f").body()).stmts().size() == 3);
    }

    @Test
    public void cpWithDce1() {
        HashMap<String, IRFuncDecl> funcMap = new HashMap<>();
        funcMap.put("f",
                new IRFuncDecl("f",
                        new IRSeq(
                                new IRMove(new IRTemp("x"), new IRTemp("y")),
                                new IRMove(new IRTemp("z"),
                                        new IRBinOp(IRBinOp.OpType.MUL,
                                                new IRTemp("x"),
                                                new IRConst(2))),
                                new IRReturn(new IRTemp("z"))
                        )));
        IRCompUnit compUnit = new IRCompUnit("compUnit", funcMap);
        IRCompUnit optimized = copyPropagationVisitor.propagateCopies(compUnit);
        optimized = deadCodeElimVisitor.run(optimized);
        IRSeq expected = new IRSeq(
                        new IRMove(new IRTemp("z"),
                        new IRBinOp(IRBinOp.OpType.MUL,
                                new IRTemp("y"),
                                new IRConst(2))),
                        new IRReturn(new IRTemp("z"))
        );
        try {
            IRGraph graph = new IRGraph(optimized.functions().get("f"));
            graph.show("CPDCETest1graph.dot");
        } catch (IOException e) {
        }
        assert(((IRSeq) optimized.functions().get("f").body()).stmts().size() == 2);
    }
}
