package kc875.asm.visit;

import com.google.common.graph.GraphBuilder;
import com.google.common.graph.MutableGraph;
import kc875.asm.*;
import kc875.asm.dfa.ASMGraph;
import kc875.asm.dfa.AvailableCopiesDFA;
import kc875.cfg.Graph;
import kc875.utils.PairAnyOrT;
import kc875.utils.SetWithInf;
import polyglot.util.Pair;

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
                    instr, setToMap(nodeToCopies.get(node).getIncludeSet())
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

    // https://www.geeksforgeeks.org/find-a-mother-vertex-in-a-graph/
    @SuppressWarnings ("UnstableApiUsage")
    private static <T> void dfsUtil(MutableGraph<T> graph, T node, HashSet<T> visited) {
        visited.add(node);
        for (T succ : graph.successors(node)) {
            if (!succ.equals(node) && !visited.contains(succ)) {
                dfsUtil(graph, succ, visited);
            }
        }
    }

    @SuppressWarnings ("UnstableApiUsage")
    public static <T> T findMother(MutableGraph<T> graph) {
        HashSet<T> visited = new HashSet<>();

        T lastNode = null;

        for (T node : graph.nodes()) {
            if (!visited.contains(node)) {
                dfsUtil(graph, node, visited);
                lastNode = node;
            }
        }

        return lastNode;
    }

    /**
     * Converts a set of "pure" pairs (see below) representing variable copies
     * to a mapping of all the elements found in pairs to their <b>final</b>
     * copies. A final copy is one that can be used to replace as many other
     * elements as possible. For example, suppose we have (a = b) and (b = c).
     * Clearly, b can replace a, but its <b>final</b> copy is c, since c can
     * replace both a and b.
     *
     * Preconditions:
     *  - The pairs are pure: they all contain two non-null values.
     *  - TODO: anmol fill these in pls I can't remember
     *
     * @param copies The set of copies, represented as pairs.
     * @param <T> The type of the pair elements.
     * @return A map from elements to their final copies.
     */
    @SuppressWarnings ("UnstableApiUsage")
    public static <T> Map<T, T> setToMap(Set<PairAnyOrT<T, T>> copies) {
        // get all the unique elements
        Set<T> temps = new HashSet<>();
        Set<Pair<T, T>> pairs = new HashSet<>();
        for (PairAnyOrT<T, T> copy : copies) {
            if (!copy.fstIsAny() && !copy.sndIsAny()) {
                // both elements are values
                T fst = copy.getFst();
                T snd = copy.getSnd();
                temps.add(fst);
                temps.add(snd);
                pairs.add(new Pair<>(fst, snd));
            } else {
                // TODO: I think this can't happen... right?
            }
        }

        // mwahahahaha
        UnionFind<T> unionFind = new UnionFind<>(temps);

        // make a second pass and merge sets for every pair
        pairs.forEach(pair -> unionFind.union(pair.part1(), pair.part2()));

        // we now have a set of connected components!

        // make a third pass (dang) and construct the directed graphs
        // these graphs need to be INVERSELY directed; see reason below

        Map<T, MutableGraph<T>> graphs = new HashMap<>();
        for (Pair<T, T> pair : pairs) {
            T fst = pair.part1();
            T snd = pair.part2();

            T parent = unionFind.find(fst); // snd is in the same set
            // find or create a graph for the parent

            MutableGraph<T> graph;
            if (graphs.containsKey(parent)) {
                graph = graphs.get(parent);
            } else {
                graph = GraphBuilder.directed().build();
                graphs.put(parent, graph);
            }
            graph.addNode(fst);
            graph.addNode(snd);
            graph.putEdge(snd, fst); // inversely directed!
        }

        // Now, we use a variant of Kosaraju's algorithm to find the mother node
        // for each graph. The mother node is actually the opposite of what we
        // want, which is why each of the above graphs had to be inversely
        // directed!
        Map<T, T> mothers = new HashMap<>();
        for (MutableGraph<T> graph : graphs.values()) {
            // these graphs are guaranteed to have mothers so this cannot be null
            T mother = findMother(graph);

            // map each temp in the graph to its mother
            for (T temp : graph.nodes()) {
                mothers.put(temp, mother);
            }
        }

        return mothers;
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
