package kc875.asm.visit;

import kc875.asm.*;
import kc875.asm.dfa.ASMAvailableCopiesDFA;
import kc875.asm.dfa.ASMGraph;
import kc875.cfg.Graph;
import kc875.utils.SetToMap;
import kc875.utils.SetWithInf;
import polyglot.util.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ASMCopyPropagationVisitor {

    public ASMCopyPropagationVisitor() {
    }

    private List<ASMInstr> copyPropagate(List<ASMInstr> func) {
        ASMGraph graph = new ASMGraph(func);
        ASMAvailableCopiesDFA dfa = new ASMAvailableCopiesDFA(graph);
        dfa.runWorklistAlgo();

        Map<Graph<ASMInstr>.Node, SetWithInf<Pair<ASMExprRT, ASMExprRT>>>
                nodeToCopies = dfa.getInMap();

        List<ASMInstr> optimFunc = new ArrayList<>();
        for (ASMInstr instr : func) {
            // Go through the ExprTemps in instr and replace by copies if they
            // exist
            Graph<ASMInstr>.Node node = graph.getNode(instr);
            optimFunc.add(replaceExprTempsWithCopiesInInstr(
                    instr, SetToMap.convert(nodeToCopies.get(node).getSet())
            ));

        }
        return optimFunc;
    }

    private ASMInstr replaceExprTempsWithCopiesInInstr(
            ASMInstr instr,
            Map<ASMExprRT, ASMExprRT> copies
    ) {
        if (instr instanceof ASMInstr_1ArgCall) {
            ASMInstr_1ArgCall i = (ASMInstr_1ArgCall) instr;
            return new ASMInstr_1ArgCall(
                    !i.destHasNewDef()
                            ? replaceExprTempsWithCopies(i.getArg(), copies)
                            : i.getArg(),
                    i.getNumParams(),
                    i.getNumRets()
            );
        } else if (instr instanceof ASMInstr_1Arg) {
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
            Map<ASMExprRT, ASMExprRT> copies
    ) {
        if (e instanceof ASMExprRT) {
            return copies.getOrDefault(e, (ASMExprRT) e);
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
