package kc875.asm.visit;

import kc875.asm.*;
import kc875.asm.dfa.ASMGraph;
import kc875.asm.dfa.AvailableCopiesDFA;
import kc875.cfg.Graph;
import kc875.utils.PairAnyOrT;
import kc875.utils.SetWithInf;

import java.util.*;

public class ASMCopyPropagationVisitor {

    public ASMCopyPropagationVisitor() {
    }

    private List<ASMInstr> copyPropagate(List<ASMInstr> func) {
        ASMGraph graph = new ASMGraph(func);
        AvailableCopiesDFA dfa = new AvailableCopiesDFA(graph);
        dfa.runWorklistAlgo();

        Map<Graph<ASMInstr>.Node, SetWithInf<PairAnyOrT<ASMExprTemp, ASMExprTemp>>>
                nodeToCopies = dfa.getInMap();

        List<ASMInstr> optimFunc = new ArrayList<>();
        for (ASMInstr instr : func) {
            // Go through the ExprTemps in instr and replace by copies if they
            // exist
            Graph<ASMInstr>.Node node = graph.getNode(instr);
            optimFunc.add(replaceExprTempsWithCopiesInInstr(
                    instr, setToMapNaive(nodeToCopies.get(node).getIncludeSet())
            ));

        }
        return optimFunc;
    }

    /**
     * Returns the first copy of lhs. Null if none found in copies.
     */
    private ASMExprTemp findCopy(
            ASMExprTemp lhs, Set<PairAnyOrT<ASMExprTemp, ASMExprTemp>> copies
    ) {
        for (PairAnyOrT<ASMExprTemp, ASMExprTemp> copy : copies) {
            if (!copy.fstIsAny() && lhs.equals(copy.getFst())) {
                // found a match
                return copy.sndIsAny() ? null : copy.getSnd();
            }
        }
        // match not found in list
        return null;
    }

    /**
     * Returns the final copy of lhs. Null if copy not found.
     *
     * @param lhs    RT to find the last copy for (to find the final rhs for).
     * @param copies set of copies.
     * @param map    map of existing copies.
     */
    private ASMExprTemp findFinalCopy(
            ASMExprTemp lhs,
            Set<PairAnyOrT<ASMExprTemp, ASMExprTemp>> copies,
            Map<ASMExprTemp, ASMExprTemp> map
    ) {
        ASMExprTemp rhs = findCopy(lhs, copies);
        if (rhs == null) return null;

        // set of RHS seen in the loop
        Set<ASMExprTemp> seenRHS = new HashSet<>();
        seenRHS.add(rhs);
        ASMExprTemp savedRHS = rhs;
        while (rhs != null) {
            savedRHS = rhs;
            if (lhs.equals(rhs)) break;
            ASMExprTemp rhsCopyInMap = map.get(rhs);
            rhs = rhsCopyInMap == null ? findCopy(rhs, copies) : rhsCopyInMap;
            if (seenRHS.contains(rhs)) break;
            seenRHS.add(rhs);
        }
        map.put(lhs, savedRHS);
        return savedRHS;
    }

    private Map<ASMExprTemp, ASMExprTemp> setToMap(
            Set<PairAnyOrT<ASMExprTemp, ASMExprTemp>> copies
    ) {
        // Convert set of pairs to a map for quick lookup
        Map<ASMExprTemp, ASMExprTemp> map = new HashMap<>();
        for (PairAnyOrT<ASMExprTemp, ASMExprTemp> copy : copies) {
            if (!copy.fstIsAny()) {
                // only put in the map if the lhs is a specific value, not *
                // Find the final copy for replacement
                ASMExprTemp lastRHS = findFinalCopy(copy.getFst(), copies, map);
                if (lastRHS != null) {
                    // RHS found
                    map.put(copy.getFst(), lastRHS);
                }
            }
        }
        return map;
    }

    private Map<ASMExprTemp, ASMExprTemp> setToMapNaive(
            Set<PairAnyOrT<ASMExprTemp, ASMExprTemp>> copies
    ) {
        Map<ASMExprTemp, ASMExprTemp> map = new HashMap<>();
        for (PairAnyOrT<ASMExprTemp, ASMExprTemp> copy : copies) {
            if (!copy.fstIsAny()) {
                // only put in the map if the lhs is a specific value, not *
                // Find the final copy for replacement
                ASMExprTemp lastRHS = findCopy(copy.getFst(), copies);
                if (lastRHS != null) {
                    // RHS found
                    map.put(copy.getFst(), lastRHS);
                }
            }
        }
        return map;
    }

    private ASMInstr replaceExprTempsWithCopiesInInstr(
            ASMInstr instr,
            Map<ASMExprTemp, ASMExprTemp> copies
    ) {
        if (instr instanceof ASMInstr_1Arg) {
            return new ASMInstr_1Arg(
                    instr.getOpCode(),
                    // only replace arg with copy if no this instr is not def
                    !instr.destHasNewDef()
                            ? replaceExprTempsWithCopies(
                            ((ASMInstr_1Arg) instr).getArg(), copies
                    )
                            : ((ASMInstr_1Arg) instr).getArg()
            );
        } else if (instr instanceof ASMInstr_2Arg) {
            return new ASMInstr_2Arg(
                    instr.getOpCode(),
                    // only replace dest with copy if no this instr is not def
                    !instr.destHasNewDef()
                            ? replaceExprTempsWithCopies(
                            ((ASMInstr_2Arg) instr).getDest(), copies
                    )
                            : ((ASMInstr_2Arg) instr).getDest(),
                    // always replace src
                    replaceExprTempsWithCopies(
                            ((ASMInstr_2Arg) instr).getSrc(), copies
                    )
            );
        }
        return instr;
    }

    private ASMExpr replaceExprTempsWithCopies(
            ASMExpr e,
            Map<ASMExprTemp, ASMExprTemp> copies
    ) {
        if (e instanceof ASMExprTemp) {
            return copies.getOrDefault(e, (ASMExprTemp) e);
        } else if (e instanceof ASMExprBinOpAdd) {
            return new ASMExprBinOpAdd(
                    replaceExprTempsWithCopies(
                            ((ASMExprBinOpAdd) e).getLeft(), copies
                    ),
                    replaceExprTempsWithCopies(
                            ((ASMExprBinOpAdd) e).getRight(), copies
                    )
            );
        } else if (e instanceof ASMExprBinOpMult) {
            return new ASMExprBinOpMult(
                    replaceExprTempsWithCopies(
                            ((ASMExprBinOpMult) e).getLeft(), copies
                    ),
                    replaceExprTempsWithCopies(
                            ((ASMExprBinOpMult) e).getRight(), copies
                    )
            );
        } else if (e instanceof ASMExprMem) {
            return new ASMExprMem(replaceExprTempsWithCopies(
                    ((ASMExprMem) e).getAddr(), copies
            ));
        }
        return e;
    }

    public List<ASMInstr> run(List<ASMInstr> instrs) {
        return ASMUtils.execPerFunc(instrs, this::copyPropagate);
    }
}
