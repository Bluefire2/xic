package kc875.asm.visit;

import com.google.common.graph.GraphBuilder;
import com.google.common.graph.MutableGraph;
import kc875.asm.*;
import kc875.utils.PairAnyOrT;
import org.junit.Test;

import static org.junit.Assert.*;

import java.util.*;

public class ASMCopyPropagationVisitorTest {
    @Test
    public void simpleMotherTest() {
        MutableGraph<Integer> graph = GraphBuilder.directed().build();
        graph.addNode(1);
        graph.addNode(2);
        graph.addNode(3);

        graph.putEdge(1, 2);
        graph.putEdge(1, 3);

        int mother = ASMCopyPropagationVisitor.findMother(graph);
        assertEquals(mother, 1);
    }

    @Test
    public void complexMotherTest() {
        MutableGraph<Integer> graph = GraphBuilder.directed().build();
        graph.addNode(1);
        graph.addNode(2);
        graph.addNode(3);
        graph.addNode(4);
        graph.addNode(5);
        graph.addNode(6);
        graph.addNode(7);

        graph.putEdge(1, 2);
        graph.putEdge(1, 3);
        graph.putEdge(2, 3);
        graph.putEdge(2, 4);
        graph.putEdge(2, 7);
        graph.putEdge(3, 6);
        graph.putEdge(4, 5);
        graph.putEdge(6, 5);
        graph.putEdge(7, 1);

        int mother = ASMCopyPropagationVisitor.findMother(graph);
        assertEquals(mother, 1);
    }

    @Test
    public void whyNotAnotherMotherTest() {
        MutableGraph<Integer> graph = GraphBuilder.directed().build();
        graph.addNode(0);
        graph.addNode(1);
        graph.addNode(2);
        graph.addNode(3);
        graph.addNode(4);
        graph.addNode(5);
        graph.addNode(6);

        graph.putEdge(0, 1);
        graph.putEdge(0, 2);
        graph.putEdge(1, 3);
        graph.putEdge(4, 1);
        graph.putEdge(5, 2);
        graph.putEdge(5, 6);
        graph.putEdge(6, 0);
        graph.putEdge(6, 4);

        int mother = ASMCopyPropagationVisitor.findMother(graph);
        assertEquals(mother, 5);
    }

    @Test
    public void simpleSetToMapTest() {
        Set<PairAnyOrT<Integer, Integer>> set = new HashSet<>(Arrays.asList(
                new PairAnyOrT<>(1, 2),
                new PairAnyOrT<>(2, 3)
        ));

        Map<Integer, Integer> map = ASMCopyPropagationVisitor.setToMap(set);
        assertEquals(map.get(1), Integer.valueOf(3));
        assertEquals(map.get(2), Integer.valueOf(3));
        assertEquals(map.get(3), Integer.valueOf(3));
    }

    @Test
    public void setToMapTest() {
        Set<PairAnyOrT<Integer, Integer>> set = new HashSet<>(Arrays.asList(
                new PairAnyOrT<>(1, 2),
                new PairAnyOrT<>(3, 2),
                new PairAnyOrT<>(2, 4),
                new PairAnyOrT<>(4, 5),
                new PairAnyOrT<>(10, 11)
        ));

        Map<Integer, Integer> map = ASMCopyPropagationVisitor.setToMap(set);
        assertEquals(map.get(1), Integer.valueOf(5));
        assertEquals(map.get(2), Integer.valueOf(5));
        assertEquals(map.get(3), Integer.valueOf(5));
        assertEquals(map.get(4), Integer.valueOf(5));
        assertEquals(map.get(5), Integer.valueOf(5));
        assertEquals(map.get(10), Integer.valueOf(11));
        assertEquals(map.get(11), Integer.valueOf(11));
    }

    @Test
    public void setToMapTestWithCycles() {
        Set<PairAnyOrT<Integer, Integer>> set = new HashSet<>(Arrays.asList(
                new PairAnyOrT<>(1, 2),
                new PairAnyOrT<>(3, 2),
                new PairAnyOrT<>(2, 4),
                new PairAnyOrT<>(4, 5),
                new PairAnyOrT<>(5, 1),
                new PairAnyOrT<>(10, 11)
        ));

        Map<Integer, Integer> map = ASMCopyPropagationVisitor.setToMap(set);

        // these have to not be null, and they all have to equal each other
        assertNotNull(map.get(1));
        assertNotNull(map.get(2));
        assertNotNull(map.get(3));
        assertNotNull(map.get(4));
        assertNotNull(map.get(5));
        // test equality:
        Set<Integer> finalNodes = new HashSet<>(Arrays.asList(
                map.get(1),
                map.get(2),
                map.get(3),
                map.get(4),
                map.get(5)
        ));
        assertEquals(finalNodes.size(), 1);

        // these are the same as before
        assertEquals(map.get(10), Integer.valueOf(11));
        assertEquals(map.get(11), Integer.valueOf(11));
    }

    @Test
    public void simpleTest0() {
        List<ASMInstr> instrs = new ArrayList<>();
        instrs.add(new ASMInstrLabel("_If"));
        instrs.add(new ASMInstr_2Arg(
                ASMOpCode.MOV,
                new ASMExprTemp("c"),
                new ASMExprTemp("d")
        ));
        instrs.add(new ASMInstr_2Arg(
                ASMOpCode.ADD,
                new ASMExprTemp("c"),
                new ASMExprTemp("d")
        ));

        ASMCopyPropagationVisitor v = new ASMCopyPropagationVisitor();
        v.run(instrs);
        System.out.println(instrs);
    }

    @Test
    public void simpleTest1() {
        List<ASMInstr> instrs = new ArrayList<>();
        instrs.add(new ASMInstrLabel("_If"));
        instrs.add(new ASMInstr_2Arg(
                ASMOpCode.MOV,
                new ASMExprTemp("c"),
                new ASMExprTemp("d")
        ));
        instrs.add(new ASMInstr_2Arg(
                ASMOpCode.ADD,
                new ASMExprTemp("c"),
                new ASMExprTemp("c")
        ));

        System.out.println("before=" + instrs);
        ASMCopyPropagationVisitor v = new ASMCopyPropagationVisitor();
        instrs = v.run(instrs);
        System.out.println("after=" + instrs);
    }

    @Test
    public void simpleTest2() {
        List<ASMInstr> instrs = new ArrayList<>();
        instrs.add(new ASMInstrLabel("_If"));
        instrs.add(new ASMInstr_2Arg(
                ASMOpCode.MOV,
                new ASMExprTemp("d"),
                new ASMExprTemp("e")
        ));
        instrs.add(new ASMInstr_2Arg(
                ASMOpCode.MOV,
                new ASMExprTemp("c"),
                new ASMExprTemp("d")
        ));
        instrs.add(new ASMInstr_2Arg(
                ASMOpCode.ADD,
                new ASMExprTemp("c"),
                new ASMExprTemp("c")
        ));

        System.out.println("before=" + instrs);
        ASMCopyPropagationVisitor v = new ASMCopyPropagationVisitor();
        instrs = v.run(instrs);
        System.out.println("after=" + instrs);
    }

    @Test
    public void simpleTest3() {
        List<ASMInstr> instrs = new ArrayList<>();
        instrs.add(new ASMInstrLabel("_If"));
        instrs.add(new ASMInstr_2Arg(
                ASMOpCode.MOV,
                new ASMExprReg("rax"),
                new ASMExprTemp("e")
        ));
        instrs.add(new ASMInstr_1Arg(
                ASMOpCode.IMUL,
                new ASMExprTemp("d")
        ));
        instrs.add(new ASMInstr_2Arg(
                ASMOpCode.MOV,
                new ASMExprTemp("c"),
                new ASMExprReg("rax")
        ));
        instrs.add(new ASMInstr_2Arg(
                ASMOpCode.ADD,
                new ASMExprTemp("c"),
                new ASMExprTemp("c")
        ));

        System.out.println("before=" + instrs);
        ASMCopyPropagationVisitor v = new ASMCopyPropagationVisitor();
        instrs = v.run(instrs);
        System.out.println("after=" + instrs);
    }

    @Test
    public void simpleTest4() {
        List<ASMInstr> instrs = new ArrayList<>();
        instrs.add(new ASMInstrLabel("_If"));
        instrs.add(new ASMInstr_2Arg(
                ASMOpCode.MOV,
                new ASMExprTemp("b"),
                new ASMExprTemp("a")
        ));
        instrs.add(new ASMInstr_2Arg(
                ASMOpCode.MOV,
                new ASMExprTemp("c"),
                new ASMExprTemp("b")
        ));
        instrs.add(new ASMInstr_2Arg(
                ASMOpCode.ADD,
                new ASMExprTemp("a"),
                new ASMExprTemp("c")
        ));

        System.out.println("before=" + instrs);
        ASMCopyPropagationVisitor v = new ASMCopyPropagationVisitor();
        instrs = v.run(instrs);
        System.out.println("after=" + instrs);
    }

    @Test
    public void simpleTest5() {
        List<ASMInstr> instrs = new ArrayList<>();
        instrs.add(new ASMInstrLabel("_If"));
        instrs.add(new ASMInstr_2Arg(
                ASMOpCode.MOV,
                new ASMExprReg("rax"),
                new ASMExprTemp("e")
        ));
        instrs.add(new ASMInstr_1Arg(
                ASMOpCode.CALL,
                new ASMExprName("_Iasdf")
        ));
        instrs.add(new ASMInstr_2Arg(
                ASMOpCode.MOV,
                new ASMExprTemp("_RET0"),
                new ASMExprReg("rax")
        ));
        instrs.add(new ASMInstr_2Arg(
                ASMOpCode.ADD,
                new ASMExprTemp("c"),
                new ASMExprTemp("c")
        ));

        System.out.println("before=" + instrs);
        ASMCopyPropagationVisitor v = new ASMCopyPropagationVisitor();
        instrs = v.run(instrs);
        System.out.println("after=" + instrs);
    }

    @Test
    public void simpleTest6() {
        List<ASMInstr> instrs = new ArrayList<>();
        instrs.add(new ASMInstrLabel("_If"));
        instrs.add(new ASMInstr_2Arg(
                ASMOpCode.MOV,
                new ASMExprTemp("_RET0"),
                new ASMExprReg("rdx")
        ));
        instrs.add(new ASMInstr_1Arg(
                ASMOpCode.CALL,
                new ASMExprName("_Iasdf")
        ));
        instrs.add(new ASMInstr_2Arg(
                ASMOpCode.MOV,
                new ASMExprTemp("_RET0"),
                new ASMExprReg("rax")
        ));
        instrs.add(new ASMInstr_2Arg(
                ASMOpCode.ADD,
                new ASMExprTemp("c"),
                new ASMExprTemp("_RET0")
        ));

        System.out.println("before=" + instrs);
        ASMCopyPropagationVisitor v = new ASMCopyPropagationVisitor();
        instrs = v.run(instrs);
        System.out.println("after=" + instrs);
    }

}
