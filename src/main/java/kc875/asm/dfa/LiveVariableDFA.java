package kc875.asm.dfa;

import com.google.common.collect.Sets;
import kc875.asm.*;
import kc875.cfg.DFAFramework;
import kc875.cfg.Graph;

import java.util.HashSet;
import java.util.Set;

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
        if (instr instanceof ASMInstr_1Arg) {
            // If the arg gets changed (arg is a reg/temp, the opCode modifies),
            // then the use set is empty. Otherwise, get vars()
            return instr.destIsDefButNoUse()
                    ? new HashSet<>()
                    : ((ASMInstr_1Arg) instr).getArg().vars();
        } else if (instr instanceof ASMInstr_2Arg) {
            // If the dest is changed (dest is a reg/temp, opCode modifies),
            // then the use set is vars(src), else vars(src) U vars(dest)
            ASMInstr_2Arg ins2 = (ASMInstr_2Arg) instr;
            Set<ASMExprRT> srcVars = ins2.getSrc().vars();
            return instr.destIsDefButNoUse()
                    ? srcVars
                    : new HashSet<>(Sets.union(ins2.getDest().vars(), srcVars));
        } else {
            return new HashSet<>();
        }
    }

    public static Set<ASMExprRT> def(Graph<ASMInstr>.Node node) {
        ASMInstr instr = node.getT();
        if (instr.getOpCode() == ASMOpCode.CALL) {
            // defines rax
            return Set.of(new ASMExprReg("rax"));
        }
        if (instr.destIsDefButNoUse()) {
            if (instr instanceof ASMInstr_1Arg) {
                return Set.of(
                        (ASMExprRT) ((ASMInstr_1Arg) instr).getArg()
                );
            } else if (instr instanceof ASMInstr_2Arg) {
                return Set.of(
                        (ASMExprRT) ((ASMInstr_2Arg) instr).getDest()
                );
            }
        }
        // dest is not changed.
        return new HashSet<>();
    }
}
