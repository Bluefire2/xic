package kc875.asm.dfa;

import kc875.asm.ASMExprRegReplaceable;
import kc875.cfg.DFAFramework;
import kc875.cfg.Graph;
import kc875.utils.SetWithInf;

/**
 * Live variable DFA (used in reg allocation). The lattice elements are sets
 * of Temps/Regs.
 */
public class LiveVariableDFA extends DFAFramework<SetWithInf<ASMExprRegReplaceable>> {

    public LiveVariableDFA(Graph graph) {
        super(
                graph,
                Direction.BACKWARD,
                (node, l) -> use(node).union(l.diff(def(node))),
                SetWithInf::union,
                new SetWithInf<>()
        );
    }

    // TODO
    public static SetWithInf<ASMExprRegReplaceable> use(Graph.Node node) {
        return new SetWithInf<>();
    }

    // TODO
    public static SetWithInf<ASMExprRegReplaceable> def(Graph.Node node) {
        return new SetWithInf<>();
    }
}