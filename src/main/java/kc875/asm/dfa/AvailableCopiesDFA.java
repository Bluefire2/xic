package kc875.asm.dfa;

import kc875.asm.*;
import kc875.cfg.DFAFramework;
import kc875.cfg.Graph;
import kc875.utils.PairAnyOrT;
import kc875.utils.SetWithInf;

/**
 * Available copies DFA (used in copy and dce). The lattice elements are
 * sets x = y, with x and y being Temps/Regs; represented as a pair(x, y).
 */
public class AvailableCopiesDFA extends
        DFAFramework<SetWithInf<PairAnyOrT<ASMExprTemp, ASMExprTemp>>, ASMInstr> {

    public AvailableCopiesDFA(ASMGraph asmGraph) {
        super(
                asmGraph,
                Direction.FORWARD,
                (node, l) -> gen(node).union(l.diff(kill(node))),
                SetWithInf::infSet,
                SetWithInf::intersect,
                SetWithInf.infSet()
        );
    }

    private static SetWithInf<PairAnyOrT<ASMExprTemp, ASMExprTemp>> gen(
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
                    return new SetWithInf<>(new PairAnyOrT<>(
                            (ASMExprTemp) ins2.getDest(),
                            (ASMExprTemp) ins2.getSrc()
                    ));
                }
            }
        }
        return new SetWithInf<>();// return empty set otherwise
    }

    private static SetWithInf<PairAnyOrT<ASMExprTemp, ASMExprTemp>> kill(
            Graph<ASMInstr>.Node node
    ) {
        ASMInstr instr = node.getT();
        if (instr instanceof ASMInstrLabel
                && ((ASMInstrLabel) instr).isFunction()) {
            // this label is for a function ==> must be the top-level
            // function's label ==> start node
            return SetWithInf.infSet();
        }

        if (instr.destHasNewDef()) {
            // dest gets defined (by 2Arg and 1Arg), kill the def
            if (instr instanceof ASMInstr_2Arg) {
                ASMInstr_2Arg ins2 = (ASMInstr_2Arg) instr;
                if (ins2.getDest() instanceof ASMExprTemp) {
                    // x = e; kill (x, z), (z, x) for any z
                    ASMExprTemp x = (ASMExprTemp) ins2.getDest();
                    return new SetWithInf<>(
                            new PairAnyOrT<>(x, null),
                            new PairAnyOrT<>(null, x)
                    );
                }
            } else if (instr instanceof ASMInstr_1Arg) {
                ASMInstr_1Arg ins1 = (ASMInstr_1Arg) instr;
                if (ins1.getArg() instanceof ASMExprTemp) {
                    // x = e; kill (x, z), (z, x) for any z
                    ASMExprTemp x = (ASMExprTemp) ins1.getArg();
                    return new SetWithInf<>(
                            new PairAnyOrT<>(x, null),
                            new PairAnyOrT<>(null, x)
                    );
                }
            }
        }
        return new SetWithInf<>();// kill nothing
    }

}
