package kc875.cfg;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * A DFA Framework with lattice elements T and graph nodes of U.
 */
public abstract class DFAFramework<T, U> {
    // Graph associated with this DFA.
    private Graph<U> graph;

    // Direction of the DFA
    public enum Direction {FORWARD, BACKWARD}

    private Direction direction;

    // Transformer function or F. Takes in the node and a lattice element,
    // and returns a lattice element.
    private BiFunction<Graph<U>.Node, T, T> F;

    // Accumulator generating function for when meet operator is applied on
    // multiple lattice elements.
    private Supplier<T> meetAcc;

    // Meet operator, combining l1 and l2 to produce l. A BinaryOperator<T>
    // is a BiFunction<T, T, T>.
    private BinaryOperator<T> meet;

    /**
     * Applies the meet operator on the lattice elements ls. Returns Optional
     * .empty() if ls is empty, otherwise reductively applies the meet operator.
     *
     * @param ls lattice elements.
     */
    public T applyMeet(Collection<T> ls) {
        return ls.stream().reduce(meetAcc.get(), meet);
    }

    // Maps from nodes to lattice elements in (after meet if applicable) and
    // out (before meet if applicable) of the node.
    private Map<Graph<U>.Node, T> inMap = new HashMap<>();
    private Map<Graph<U>.Node, T> outMap = new HashMap<>();

    /**
     * Initialize the DFA Framework, with all nodes' inMap and outMap
     * initialized to lattice element top.
     *
     * @param graph     graph associated with this DFA.
     * @param direction direction of DFA.
     * @param F         transformer function.
     * @param meetAcc   function that creates an accumulator when applying meet.
     * @param meet      meet operator.
     * @param top       top lattice element for initialization.
     */
    public DFAFramework(Graph<U> graph,
                        Direction direction,
                        BiFunction<Graph<U>.Node, T, T> F,
                        Supplier<T> meetAcc,
                        BinaryOperator<T> meet,
                        T top
    ) {
        this.graph = graph;
        this.direction = direction;
        this.F = F;
        this.meetAcc = meetAcc;
        this.meet = meet;
        for (Graph<U>.Node node : graph.getAllNodes()) {
            inMap.put(node, top);
            outMap.put(node, top);
        }
    }

    public Graph getGraph() {
        return graph;
    }

    public Map<Graph<U>.Node, T> getInMap() {
        return inMap;
    }

    public Map<Graph<U>.Node, T> getOutMap() {
        return outMap;
    }

    public T inputToF(Graph<U>.Node node) {
        switch (direction) {
            case FORWARD:
                return applyMeet(node.pred().stream()
                        .map(outMap::get).collect(Collectors.toSet()));
            case BACKWARD:
                return applyMeet(node.succ().stream()
                        .map(inMap::get).collect(Collectors.toSet()));
            default:
                throw new IllegalAccessError("Weird direction in DFA");
        }
    }

    public T afterF(Graph<U>.Node node) {
        return F.apply(node, inputToF(node));
    }

    public void runWorklistAlgo() {
        // already initialized to top

        // worklist <- { all nodes }
        Set<Graph<U>.Node> worklist = graph.getAllNodes();

        // Invariant: all nodes with unsatisfied eqns in worklist
        for (Iterator<Graph<U>.Node> iter = worklist.iterator(); iter.hasNext();) {
            Graph<U>.Node node = iter.next();
            iter.remove();

            switch (direction) {
                case FORWARD:
                    T outBeforeUpdate = outMap.get(node);
                    T outAfterUpdate = this.afterF(node);
                    if (!outBeforeUpdate.equals(outAfterUpdate)) {
                        // out has changed, push succs of node to worklist
                        worklist.addAll(node.succ());
                    }
                    break;
                case BACKWARD:
                    T inBeforeUpdate = inMap.get(node);
                    T inAfterUpdate = this.afterF(node);
                    if (!inBeforeUpdate.equals(inAfterUpdate)) {
                        // in has changed, push preds of node to worklist
                        worklist.addAll(node.pred());
                    }
            }
        }
    }
}
