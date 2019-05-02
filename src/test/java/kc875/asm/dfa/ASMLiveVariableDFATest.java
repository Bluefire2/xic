package kc875.asm.dfa;

import kc875.asm.*;
import kc875.asm.visit.RegAllocationColoringVisitorTest;
import kc875.cfg.Graph;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ASMLiveVariableDFATest {

    @Test
    public void simpleASMTest0() {
        List<ASMInstr> instrs = new ArrayList<>();
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
        ASMLiveVariableDFA dfa = new ASMLiveVariableDFA(graph);
        dfa.runWorklistAlgo();

        instrs.forEach(System.out::println);
        System.out.println("results:");
        for (Graph<ASMInstr>.Node node : graph.getAllNodes()) {
            System.out.println(node);
            System.out.println("\tlive vars in=" + dfa.getInMap().get(node));
            System.out.println("\tlive vars out=" + dfa.getOutMap().get(node));
        }
        System.out.println();
    }

    @Test
    public void simpleASMTest1() {
        List<ASMInstr> instrs = new ArrayList<>();
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
        ASMLiveVariableDFA dfa = new ASMLiveVariableDFA(graph);
        dfa.runWorklistAlgo();

        instrs.forEach(System.out::println);
        System.out.println("results:");
        for (Graph<ASMInstr>.Node node : graph.getAllNodes()) {
            System.out.println(node);
            System.out.println("\tlive vars in=" + dfa.getInMap().get(node));
            System.out.println("\tlive vars out=" + dfa.getOutMap().get(node));
        }
        System.out.println();
    }

    @Test
    public void dannyTestForColoringVisitorTest() {
        List<ASMInstr> instrs =
                RegAllocationColoringVisitorTest.getTestInstrsPrecolored(7);
        ASMGraph graph = new ASMGraph(instrs);
        ASMLiveVariableDFA dfa = new ASMLiveVariableDFA(graph);
        dfa.runWorklistAlgo();

        instrs.forEach(System.out::println);
        try {
            dfa.show("./dannyTestForColoringVisitorTest.dot");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}