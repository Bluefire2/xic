package kc875.asm.dfa;

import kc875.asm.*;
import kc875.cfg.Graph;
import kc875.utils.SetWithInf;
import org.junit.Test;
import polyglot.util.Pair;

import java.util.ArrayList;
import java.util.List;

public class ASMAvailableCopiesDFATest {

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
        ASMAvailableCopiesDFA dfa = new ASMAvailableCopiesDFA(graph);
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
        ASMAvailableCopiesDFA dfa = new ASMAvailableCopiesDFA(graph);
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
        ASMAvailableCopiesDFA dfa = new ASMAvailableCopiesDFA(graph);
        dfa.runWorklistAlgo();

        instrs.forEach(System.out::println);
        System.out.println("results:");
        for (Graph<ASMInstr>.Node node : graph.getAllNodes()) {
            System.out.println(node);
            System.out.println("\tAC in=" + dfa.getInMap().get(node));
            System.out.println("\tAC out=" + dfa.getOutMap().get(node));
        }
        System.out.println();


        System.out.println();
        SetWithInf<Pair<ASMExprTemp, ASMExprTemp>> s = new SetWithInf<>();
        Pair<ASMExprTemp, ASMExprTemp> p1 = new Pair<>(
                new ASMExprTemp("a"), new ASMExprTemp("b")
        );
        s.add(p1);

        System.out.println("after adding p1=" + s);
        s.removeIf(p -> p.part1().equals(new ASMExprTemp("a")));
        System.out.println("after removing p2=" + s);
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
        ASMAvailableCopiesDFA dfa = new ASMAvailableCopiesDFA(graph);
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

