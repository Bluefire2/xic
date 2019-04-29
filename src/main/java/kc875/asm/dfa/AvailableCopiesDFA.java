package kc875.asm.dfa;

import com.google.common.collect.Sets;
import kc875.asm.*;
import kc875.cfg.DFAFramework;
import kc875.cfg.Graph;
import polyglot.util.Pair;

import java.util.HashSet;
import java.util.Set;

/**
 * Available copies DFA (used in copy and dce). The lattice elements are
 * sets x = y, with x and y being Temps/Regs; represented as a pair(x, y).
 */
public class AvailableCopiesDFA extends
        DFAFramework<Set<Pair<AnyOrT<ASMExprRT>, AnyOrT<ASMExprRT>>>,
                ASMInstr> {

    public AvailableCopiesDFA(ASMGraph asmGraph) {
        super(
                asmGraph,
                Direction.FORWARD,
                (node, l) -> new HashSet<>(Sets.union(
                        gen(node),
                        Sets.difference(l, kill(node))
                )),
                () -> Sets.newHashSet(new Pair<>(new AnyOrT<>(), new AnyOrT<>())),
                (l1, l2) -> new HashSet<>(Sets.union(l1, l2)),
                Sets.newHashSet(new Pair<>(new AnyOrT<>(), new AnyOrT<>()))
        );
    }

    private static Set<Pair<AnyOrT<ASMExprRT>, AnyOrT<ASMExprRT>>> gen(
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
                    return Sets.newHashSet(new Pair<>(
                            new AnyOrT<>((ASMExprRT) ins2.getDest()),
                            new AnyOrT<>((ASMExprRT) ins2.getSrc())
                    ));
                }
            }
        }
        return new HashSet<>();// return empty set otherwise
    }

    private static Set<Pair<AnyOrT<ASMExprRT>, AnyOrT<ASMExprRT>>> kill(
            Graph<ASMInstr>.Node node
    ) {
        ASMInstr instr = node.getT();
        if (instr instanceof ASMInstr_2Arg) {
            if (instr.getOpCode() == ASMOpCode.MOV
                    || instr.getOpCode() == ASMOpCode.MOVZX) {
                ASMInstr_2Arg ins2 = (ASMInstr_2Arg) instr;
                if (ins2.getDest() instanceof ASMExprRT) {
                    // x = e; kill (x, z), (z, x) for any z
                    ASMExprRT x = (ASMExprRT) ins2.getDest();
                    HashSet<Pair<AnyOrT<ASMExprRT>, AnyOrT<ASMExprRT>>> s =
                            new HashSet<>();
                    s.add(new Pair<>(
                            new AnyOrT<>(x),
                            new AnyOrT<>()
                    ));
                    s.add(new Pair<>(
                            new AnyOrT<>(),
                            new AnyOrT<>(x)
                    ));
                    return s;
                }
            }
        } else if (instr instanceof ASMInstrLabel
                && ((ASMInstrLabel) instr).isFunction()) {
            // this label is for a function ==> must be the top-level
            // function's label ==> start node
            return Sets.newHashSet(new Pair<>(new AnyOrT<>(), new AnyOrT<>()));
        }
        return new HashSet<>();// kill nothing
    }
}

/**
 * A wrapper that represents any particular instantiation of T or any
 * instantiation (like "a" or {set of all strings}).
 */
// TODO: problem: "any" could be anything in the world; no way to know what
//  type that "any" has ==> so no way to differentiate any of int and any of
//  string. Could do this, but too much work:
//    https://stackoverflow.com/questions/9931611/determine-if-generic-types-are-equal
class AnyOrT<T> {
    // Either contains something or null. The latter implies could be anything
    private T t;

    /**
     * Constructs an 'any' representation of T.
     */
    AnyOrT() {
        t = null;
    }

    /**
     * Specific value of T given by t.
     */
    AnyOrT(T t) {
        this.t = t;
    }

    public boolean isAny() {
        return t == null;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof AnyOrT<?>)) {
            return false;
        }
        AnyOrT<?> other = (AnyOrT<?>) o;
        if (this.isAny() || other.isAny()) {
            // either is any
            return true;
        }
        return t.equals(other.t);
    }

    @Override
    public int hashCode() {
        return this.isAny() ? "any".hashCode() : t.hashCode();
    }
}
