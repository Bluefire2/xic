package kc875.utils;

import com.google.common.graph.GraphBuilder;
import com.google.common.graph.MutableGraph;
import polyglot.util.Pair;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class SetToMap {
    // https://www.geeksforgeeks.org/find-a-mother-vertex-in-a-graph/
    @SuppressWarnings("UnstableApiUsage")
    private static <T> void dfsUtil(MutableGraph<T> graph, T node, HashSet<T> visited) {
        visited.add(node);
        for (T succ : graph.successors(node)) {
            if (!succ.equals(node) && !visited.contains(succ)) {
                dfsUtil(graph, succ, visited);
            }
        }
    }

    @SuppressWarnings("UnstableApiUsage")
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
     * Note: all redundant copy pairs (pairs (A, B) where A equals B) are
     * ignored during this conversion.
     *
     * @param copies The set of copies, represented as pairs.
     * @param <T>    The type of the pair elements.
     * @return A map from elements to their final copies.
     */
    @SuppressWarnings("UnstableApiUsage")
    public static <T> Map<T, T> convert(Set<Pair<T, T>> copies) {
        // get all the unique elements
        Set<T> temps = new HashSet<>();
        Set<Pair<T, T>> pairs = new HashSet<>();
        for (Pair<T, T> copy : copies) {
            // both elements are values
            T fst = copy.part1();
            T snd = copy.part2();

            // filter out self-pairs
            if (!fst.equals(snd)) {
                temps.add(fst);
                temps.add(snd);
                pairs.add(new Pair<>(fst, snd));
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

}
