package kc875.asm.dfa;

import kc875.asm.ASMExprRegReplaceable;
import kc875.asm.ASMInstr;
import kc875.asm.ASMInstr_1Arg;
import kc875.asm.ASMInstr_2Arg;
import kc875.cfg.DFAFramework;
import kc875.cfg.Graph;
import kc875.utils.SetWithInf;

/**
 * Live variable DFA (used in reg allocation). The lattice elements are sets
 * of Temps/Regs.
 */
public class LiveVariableDFA extends DFAFramework<SetWithInf<ASMExprRegReplaceable>, ASMInstr> {

    public LiveVariableDFA(ASMGraph asmGraph) {
        super(
                asmGraph,
                Direction.BACKWARD,
                (node, l) -> use(node).union(l.diff(def(node))),
                SetWithInf::union,
                new SetWithInf<>()
        );
    }

    // TODO
    public static SetWithInf<ASMExprRegReplaceable> use(Graph<ASMInstr>.Node node) {
        ASMInstr instr = node.getT();
        if (instr instanceof ASMInstr_1Arg) {
            // TODO
        } else if (instr instanceof ASMInstr_2Arg) {
            // TODO
        } else {
            return new SetWithInf<>();
        }
    }

    // TODO
    public static SetWithInf<ASMExprRegReplaceable> def(Graph<ASMInstr>.Node node) {
        return new SetWithInf<>();
    }
}
