package kc875.asm.dfa;

import kc875.asm.*;
import kc875.cfg.DFAFramework;
import kc875.cfg.Graph;
import kc875.utils.SetWithInf;
import polyglot.util.Pair;

import java.util.HashSet;
import java.util.Set;

/**
 * Available copies DFA (used in copy and dce). The lattice elements are
 * sets x = y, with x and y being Temps/Regs; represented as a pair(x, y).
 */
public class AvailableCopiesDFA extends
        DFAFramework<SetWithInf<Pair<ASMExprTemp, ASMExprTemp>>, ASMInstr> {

    public AvailableCopiesDFA(ASMGraph asmGraph) {
        super(
                asmGraph,
                Direction.FORWARD,
                (node, l) -> {
                    Pair<Set<ASMExprTemp>, Set<ASMExprTemp>> kill = kill(node);
                    // Remove all elements from l with l.part1() = k for
                    // all k in kill.part1()
                    kill.part1().forEach(k ->
                            l.removeIf(p -> p.part1().equals(k))
                    );
                    // Remove all elements from l with l.part2() = k for
                    // all k in kill.part2()
                    kill.part2().forEach(k ->
                            l.removeIf(p -> p.part2().equals(k))
                    );
                    return gen(node).union(l);
                },
                SetWithInf::infSet,
                SetWithInf::intersect,
                SetWithInf.infSet()
        );
    }

    private static SetWithInf<Pair<ASMExprTemp, ASMExprTemp>> gen(
            Graph<ASMInstr>.Node node
    ) {
        ASMInstr instr = node.getT();
        if (instr instanceof ASMInstr_2Arg) {
            if (instr.getOpCode() == ASMOpCode.MOV
                    || instr.getOpCode() == ASMOpCode.MOVZX) {
                ASMInstr_2Arg ins2 = (ASMInstr_2Arg) instr;
                if (ins2.getDest() instanceof ASMExprTemp
                        && ins2.getSrc() instanceof ASMExprTemp) {
                    // x = y; gen (x, y)
                    return new SetWithInf<>(new Pair<>(
                            (ASMExprTemp) ins2.getDest(),
                            (ASMExprTemp) ins2.getSrc()
                    ));
                }
            }
        }
        return new SetWithInf<>();// return empty set otherwise
    }

    /**
     * Returns a pair (s1, s2) of sets, where s1 represents the set of temps t
     * that will kill copies (t, *). Similarly for s2.
     */
    private static Pair<Set<ASMExprTemp>, Set<ASMExprTemp>> kill(
            Graph<ASMInstr>.Node node
    ) {
        ASMInstr instr = node.getT();
        if (instr instanceof ASMInstrLabel
                && ((ASMInstrLabel) instr).isFunction()) {
            // this label is for a function ==> must be the top-level
            // function's label ==> start node
            return new Pair<>(new HashSet<>(), new HashSet<>());
        }

        if (instr.destHasNewDef()) {
            // dest gets defined (by 2Arg and 1Arg), kill the def
            if (instr instanceof ASMInstr_2Arg) {
                ASMInstr_2Arg ins2 = (ASMInstr_2Arg) instr;
                if (ins2.getDest() instanceof ASMExprTemp) {
                    // x = e; kill (x, z), (z, x) for any z
                    ASMExprTemp x = (ASMExprTemp) ins2.getDest();
                    return new Pair<>(
                            new HashSet<>(Set.of(x)),
                            new HashSet<>(Set.of(x))
                    );
                }
            } else if (instr instanceof ASMInstr_1Arg) {
                ASMInstr_1Arg ins1 = (ASMInstr_1Arg) instr;
                if (ins1.getArg() instanceof ASMExprTemp) {
                    // x = e; kill (x, z), (z, x) for any z
                    ASMExprTemp x = (ASMExprTemp) ins1.getArg();
                    return new Pair<>(
                            new HashSet<>(Set.of(x)),
                            new HashSet<>(Set.of(x))
                    );
                }
            }
        }
        return new Pair<>(new HashSet<>(), new HashSet<>());// kill nothing
    }

}
