package kc875.asm.dfa;

import com.google.common.collect.Sets;
import kc875.asm.ASMExprRT;
import kc875.asm.ASMInstr;
import kc875.asm.ASMInstr_1Arg;
import kc875.asm.ASMInstr_2Arg;
import kc875.cfg.DFAFramework;
import kc875.cfg.Graph;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Live variable DFA (used in reg allocation). The lattice elements are sets
 * of Temps/Regs.
 */
public class LiveVariableDFA extends DFAFramework<Set<ASMExprRT>,
        ASMInstr> {

    public LiveVariableDFA(ASMGraph asmGraph) {
        super(
                asmGraph,
                Direction.BACKWARD,
                (node, l) -> new HashSet<>(Sets.union(
                        use(node),
                        Sets.difference(l, def(node))
                )),
                HashSet::new,
                (l1, l2) -> new HashSet<>(Sets.union(l1, l2)),
                new HashSet<>()
        );
    }

    public static Set<ASMExprRT> use(Graph<ASMInstr>.Node node) {
        ASMInstr instr = node.getT();
        Set<ASMExprRT> s = instr.implicitUsedRegs().stream()
                .map(r -> (ASMExprRT) r).collect(Collectors.toSet());

        if (instr instanceof ASMInstr_1Arg) {
            // If the arg gets changed (arg is a reg/temp, the opCode modifies),
            // then the use set is empty. Otherwise, get vars()
            if (instr.destIsDefButNoUse())
                return s;
            s.addAll(((ASMInstr_1Arg) instr).getArg().vars());
            return s;
        } else if (instr instanceof ASMInstr_2Arg) {
            // If the dest is changed (dest is a reg/temp, opCode modifies),
            // then the use set is vars(src), else vars(src) U vars(dest)
            ASMInstr_2Arg ins2 = (ASMInstr_2Arg) instr;
            s.addAll(ins2.getSrc().vars());
            if (instr.destIsDefButNoUse())
                return s;
            s.addAll(ins2.getDest().vars());
            return s;
        } else {
            return s;
        }
    }

    public static Set<ASMExprRT> def(Graph<ASMInstr>.Node node) {
        ASMInstr instr = node.getT();
        Set<ASMExprRT> s = instr.implicitDefRegs().stream()
                .map(r -> (ASMExprRT) r).collect(Collectors.toSet());

        if (instr.destHasNewDef()) {
            if (instr instanceof ASMInstr_1Arg) {
                s.addAll(((ASMInstr_1Arg) instr).getArg().vars());
                return s;
            } else if (instr instanceof ASMInstr_2Arg) {
                s.addAll(((ASMInstr_2Arg) instr).getDest().vars());
                return s;
            }
        }
        // dest is not changed.
        return s;
    }
}
