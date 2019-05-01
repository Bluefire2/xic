package kc875.asm.visit;

import kc875.asm.*;
import kc875.asm.dfa.ASMGraph;
import kc875.asm.dfa.LiveVariableDFA;
import kc875.cfg.Graph;

import java.util.*;

public class ASMDeadCodeEliminationVisitor {

    public ASMDeadCodeEliminationVisitor() {
    }

    private List<ASMInstr> elimDeadCode(List<ASMInstr> func) {
        ASMGraph graph = new ASMGraph(func);
        LiveVariableDFA dfa = new LiveVariableDFA(graph);
        dfa.runWorklistAlgo();

        Map<Graph<ASMInstr>.Node, Set<ASMExprRT>> nodeToLiveVars =
                dfa.getOutMap();

        List<ASMInstr> optimFunc = new ArrayList<>();
        for (ASMInstr instr : func) {
            // If for all defined variables v, v is not live out, skip this
            // instr
            Graph<ASMInstr>.Node node = graph.getNode(instr);

            Set<ASMExprRT> allDefs = new HashSet<>(instr.implicitDefRegs());
            if (instr.destHasNewDef()) {
                if (instr instanceof ASMInstr_2Arg) {
                    allDefs.add((ASMExprRT) ((ASMInstr_2Arg) instr).getDest());
                    boolean allDefsUseless = allDefs.stream()
                            .noneMatch(def -> nodeToLiveVars.get(node).contains(def));
                    if (!allDefsUseless)
                        optimFunc.add(instr);

                } else if (instr instanceof ASMInstr_1Arg) {
                    allDefs.add((ASMExprRT) ((ASMInstr_1Arg) instr).getArg());
                    boolean allDefsUseless = allDefs.stream()
                            .noneMatch(def -> nodeToLiveVars.get(node).contains(def));
                    if (!allDefsUseless)
                        optimFunc.add(instr);
                } else {
                    optimFunc.add(instr);
                }
            } else {
                optimFunc.add(instr);
            }
        }

        return optimFunc;
    }

    public List<ASMInstr> run(List<ASMInstr> instrs) {
        return ASMUtils.execPerFunc(instrs, this::elimDeadCode);
    }
}
