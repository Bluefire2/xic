package kc875.asm.dfa;

import kc875.asm.*;
import kc875.cfg.Graph;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class AvailableCopiesDFATest {

    @Test
    public void simpleASMTest0() {
        List<ASMInstr> instrs = new ArrayList<>();
        instrs.add(new ASMInstrLabel("_If"));
        instrs.add(new ASMInstr_2Arg(
                ASMOpCode.ADD,
                new ASMExprTemp("a"),
                new ASMExprTemp("b")
        ));
        instrs.add(new ASMInstr_2Arg(
                ASMOpCode.MOV,
                new ASMExprTemp("c"),
                new ASMExprTemp("d")
        ));

        ASMGraph graph = new ASMGraph(instrs);
        AvailableCopiesDFA dfa = new AvailableCopiesDFA(graph);
        dfa.runWorklistAlgo();

        instrs.forEach(System.out::println);
        System.out.println("results:");
        for (Graph<ASMInstr>.Node node : graph.getAllNodes()) {
            System.out.println(node);
            System.out.println("\tAC in=" + dfa.getInMap().get(node));
            System.out.println("\tAC out=" + dfa.getOutMap().get(node));
        }
        System.out.println();
    }

    @Test
    public void simpleASMTest1() {
        List<ASMInstr> instrs = new ArrayList<>();
        instrs.add(new ASMInstrLabel("_If"));
        instrs.add(new ASMInstr_2Arg(
                ASMOpCode.MOV,
                new ASMExprTemp("c"),
                new ASMExprTemp("d")
        ));
        instrs.add(new ASMInstr_2Arg(
                ASMOpCode.MOV,
                new ASMExprTemp("a"),
                new ASMExprTemp("b")
        ));
        instrs.add(new ASMInstr_2Arg(
                ASMOpCode.MOV,
                new ASMExprReg("rax"),
                new ASMExprTemp("a")
        ));

        ASMGraph graph = new ASMGraph(instrs);
        AvailableCopiesDFA dfa = new AvailableCopiesDFA(graph);
        dfa.runWorklistAlgo();

        instrs.forEach(System.out::println);
        System.out.println("results:");
        for (Graph<ASMInstr>.Node node : graph.getAllNodes()) {
            System.out.println(node);
            System.out.println("\tAC in=" + dfa.getInMap().get(node));
            System.out.println("\tAC out=" + dfa.getOutMap().get(node));
        }
        System.out.println();
    }

    @Test
    public void simpleASMTest2() {
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

        ASMGraph graph = new ASMGraph(instrs);
        AvailableCopiesDFA dfa = new AvailableCopiesDFA(graph);
        dfa.runWorklistAlgo();

        instrs.forEach(System.out::println);
        System.out.println("results:");
        for (Graph<ASMInstr>.Node node : graph.getAllNodes()) {
            System.out.println(node);
            System.out.println("\tAC in=" + dfa.getInMap().get(node));
            System.out.println("\tAC out=" + dfa.getOutMap().get(node));
        }
        System.out.println();
    }

    @Test
    public void simpleASMTest3() {
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

        ASMGraph graph = new ASMGraph(instrs);
        AvailableCopiesDFA dfa = new AvailableCopiesDFA(graph);
        dfa.runWorklistAlgo();

        instrs.forEach(System.out::println);
        System.out.println("results:");
        for (Graph<ASMInstr>.Node node : graph.getAllNodes()) {
            System.out.println(node);
            System.out.println("\tAC in=" + dfa.getInMap().get(node));
            System.out.println("\tAC out=" + dfa.getOutMap().get(node));
        }
        System.out.println();
    }
}

