package kc875.asm.dfa;

import kc875.asm.*;
import kc875.cfg.DFAFramework;
import kc875.cfg.Graph;
import kc875.utils.AnyOrT;
import kc875.utils.SetWithInf;
import polyglot.util.Pair;

/**
 * Available copies DFA (used in copy and dce). The lattice elements are
 * sets x = y, with x and y being Temps/Regs; represented as a pair(x, y).
 */
public class AvailableCopiesDFA extends
        DFAFramework<SetWithInf<Pair<AnyOrT<ASMExprRT>, AnyOrT<ASMExprRT>>>,
                ASMInstr> {

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

    private static SetWithInf<Pair<AnyOrT<ASMExprRT>, AnyOrT<ASMExprRT>>> gen(
            Graph<ASMInstr>.Node node
    ) {
        ASMInstr instr = node.getT();
        if (instr instanceof ASMInstr_2Arg) {
            if (instr.getOpCode() == ASMOpCode.MOV
                    || instr.getOpCode() == ASMOpCode.MOVZX) {
                ASMInstr_2Arg ins2 = (ASMInstr_2Arg) instr;
                if (ins2.getDest() instanceof ASMExprRT
                        && ins2.getSrc() instanceof ASMExprRT) {
                    // x = y; gen (x, y)
                    return new SetWithInf<>(new Pair<>(
                            new AnyOrT<>((ASMExprRT) ins2.getDest()),
                            new AnyOrT<>((ASMExprRT) ins2.getSrc())
                    ));
                }
            }
        }
        return new SetWithInf<>();// return empty set otherwise
    }

    private static SetWithInf<Pair<AnyOrT<ASMExprRT>, AnyOrT<ASMExprRT>>> kill(
            Graph<ASMInstr>.Node node
    ) {
        ASMInstr instr = node.getT();
        if (hasNewDef(instr)) {
            if (instr instanceof ASMInstr_2Arg) {
                ASMInstr_2Arg ins2 = (ASMInstr_2Arg) instr;
                if (ins2.getDest() instanceof ASMExprRT) {
                    // x = e; kill (x, z), (z, x) for any z
                    ASMExprRT x = (ASMExprRT) ins2.getDest();
                    return new SetWithInf<>(
                            new Pair<>(new AnyOrT<>(x), new AnyOrT<>()),
                            new Pair<>(new AnyOrT<>(), new AnyOrT<>(x))
                    );
                }
            }
        } else if (instr instanceof ASMInstrLabel
                && ((ASMInstrLabel) instr).isFunction()) {
            // this label is for a function ==> must be the top-level
            // function's label ==> start node
            return SetWithInf.infSet();
        }
        return new SetWithInf<>();// kill nothing
    }

    /**
     * Returns true if instr creates a new definition on the destination (if
     * exists), false otherwise.
     */
    private static boolean hasNewDef(ASMInstr instr) {
        switch (instr.getOpCode()) {
            case ADD:
            case SUB:
            case IMUL:
            case IDIV:
            case AND:
            case OR:
            case XOR:
            case SHR:
            case SHL:
            case SAR:
            case MOV:
            case MOVZX:
            case INC:
            case DEC:
            case NOT:
            case POP:
            case SETE:
            case SETNE:
            case SETG:
            case SETGE:
            case SETL:
            case SETLE:
                return true;
            default:
                return false;
        }
    }
}
