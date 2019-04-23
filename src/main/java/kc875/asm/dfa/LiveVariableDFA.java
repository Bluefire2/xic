package kc875.asm.dfa;

import com.google.common.collect.Sets;
import kc875.asm.ASMExprRegReplaceable;
import kc875.asm.ASMInstr;
import kc875.asm.ASMInstr_1Arg;
import kc875.asm.ASMInstr_2Arg;
import kc875.cfg.DFAFramework;
import kc875.cfg.Graph;

import java.util.HashSet;
import java.util.Set;

/**
 * Live variable DFA (used in reg allocation). The lattice elements are sets
 * of Temps/Regs.
 */
public class LiveVariableDFA extends DFAFramework<Set<ASMExprRegReplaceable>,
        ASMInstr> {

    public LiveVariableDFA(ASMGraph asmGraph) {
        super(
                asmGraph,
                Direction.BACKWARD,
                (node, l) -> Sets.union(
                        use(node),
                        Sets.difference(l, def(node)).immutableCopy()
                ),
                (l1, l2) -> Sets.union(l1, l2).immutableCopy(),
                new HashSet<>()
        );
    }

    // TODO
    public static Set<ASMExprRegReplaceable> use(Graph<ASMInstr>.Node node) {
        ASMInstr instr = node.getT();
        if (instr instanceof ASMInstr_1Arg) {
            // TODO
        } else if (instr instanceof ASMInstr_2Arg) {
            // TODO
        } else {
            return new HashSet<>();
        }
    }

    // TODO
    public static Set<ASMExprRegReplaceable> def(Graph<ASMInstr>.Node node) {
        return new HashSet<>();
    }
}
