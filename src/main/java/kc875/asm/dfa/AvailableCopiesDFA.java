package kc875.asm.dfa;

import com.google.common.collect.Sets;
import kc875.asm.*;
import kc875.cfg.DFAFramework;
import kc875.cfg.Graph;
import kc875.utils.SetWithInf;
import polyglot.util.Pair;

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
                // lDiffKill is not inf (postcondition), so the
                // precondition of union is met
                (node, l) -> gen(node).union(lDiffKill(l, node)),
                SetWithInf::infSet,// meet acc
                (l1, l2) -> {
                    if (l1.isInf()) // l1 is top
                        return l2;
                    if (l2.isInf()) // l2 is top
                        return l1;
                    // l1 and l2 are not top, take the normal intersection
                    return new SetWithInf<>(Sets.intersection(
                            l1.getSet(), l2.getSet()
                    ));
                },
                SetWithInf.infSet()// top
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
     * Returns the result of l.diff(kill(node)) for this analysis.
     * Postconditions:
     * - The returned set is not infinite.
     */
    private static SetWithInf<Pair<ASMExprTemp, ASMExprTemp>> lDiffKill(
            SetWithInf<Pair<ASMExprTemp, ASMExprTemp>> l,
            Graph<ASMInstr>.Node node
    ) {
        ASMInstr instr = node.getT();
        if (instr instanceof ASMInstrLabel
                && ((ASMInstrLabel) instr).isFunction()) {
            // this label is for a function ==> must be the top-level
            // function's label ==> start node; return empty set
            return new SetWithInf<>();
        }

        if (l.isInf())// l is top
            // Note: in this analysis, the meet function is intersection and
            // the start node kills everything. So all nodes except the start
            // node effectively have their tops initialized to empty set
            // Ask Anmol if more explanation needed
            return new SetWithInf<>();// kill everything

        if (instr.destHasNewDef()) {
            // dest gets defined (by 2Arg and 1Arg), kill the def
            if (instr instanceof ASMInstr_2Arg) {
                ASMInstr_2Arg ins2 = (ASMInstr_2Arg) instr;
                if (ins2.getDest() instanceof ASMExprTemp) {
                    // x = e; kill (x, z), (z, x) for any z
                    ASMExprTemp x = (ASMExprTemp) ins2.getDest();
                    // Remove all elements from l with l.part1() = x
                    l.removeIf(p -> p.part1().equals(x));
                    // Remove all elements from l with l.part2() = x
                    l.removeIf(p -> p.part2().equals(x));
                    return l;
                }
            } else if (instr instanceof ASMInstr_1Arg) {
                ASMInstr_1Arg ins1 = (ASMInstr_1Arg) instr;
                if (ins1.getArg() instanceof ASMExprTemp) {
                    // x = e; kill (x, z), (z, x) for any z
                    ASMExprTemp x = (ASMExprTemp) ins1.getArg();
                    // Remove all elements from l with l.part1() = x
                    l.removeIf(p -> p.part1().equals(x));
                    // Remove all elements from l with l.part2() = x
                    l.removeIf(p -> p.part2().equals(x));
                    return l;
                }
            }
        }
        return l;// kill nothing
    }

}
