package kc875.cfg;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;

/**
 * A DFA Framework with lattice elements T and graph nodes of U.
 */
public abstract class DFAFramework<T, U> {
    // Graph associated with this DFA.
    protected Graph<U> graph;

    // Direction of the DFA
    public enum Direction {FORWARD, BACKWARD}
    protected Direction direction;

    // Transformer function or F. Takes in the node and a lattice element,
    // and returns a lattice element.
    protected BiFunction<Graph<U>.Node, T, T> F;

    // Meet operator, combining l1 and l2 to produce l. A BinaryOperator<T>
    // is a BiFunction<T, T, T>.
    protected BinaryOperator<T> meet;

    /**
     * Applies the meet operator on the lattice elements ls. Returns Optional
     * .empty() if ls is empty, otherwise reductively applies the meet operator.
     * @param ls lattice elements.
     */
    public Optional<T> applyMeet(Collection<T> ls) {
        return ls.stream().reduce(meet);
    }

    // Maps from nodes to lattice elements in (after meet if applicable) and
    // out (before meet if applicable) of the node.
    protected Map<Graph.Node, T> inMap = new HashMap<>();
    protected Map<Graph.Node, T> outMap = new HashMap<>();

    /**
     * Initialize the DFA Framework, with all nodes' inMap and outMap
     * initialized to lattice element top.
     * @param graph graph associated with this DFA.
     * @param direction direction of DFA.
     * @param F transformer function.
     * @param meet meet operator.
     * @param top top lattice element for initialization.
     */
    public DFAFramework(Graph<U> graph,
                        Direction direction,
                        BiFunction<Graph<U>.Node, T, T> F,
                        BinaryOperator<T> meet,
                        T top
                        ) {
        this.graph = graph;
        this.direction = direction;
        this.F = F;
        this.meet = meet;
        for (Graph.Node node : graph.getAllNodes()) {
            inMap.put(node, top);
            outMap.put(node, top);
        }
    }

    public Graph getGraph() {
        return graph;
    }

    public Map<Graph.Node, T> getInMap() {
        return inMap;
    }

    public Map<Graph.Node, T> getOutMap() {
        return outMap;
    }
}
