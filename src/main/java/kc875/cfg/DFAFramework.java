package kc875.cfg;

import java.io.IOException;
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
    private T applyMeet(Collection<T> ls) {
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
        return new HashMap<>(inMap);
    }

    public Map<Graph<U>.Node, T> getOutMap() {
        return new HashMap<>(outMap);
    }

    private T inputToF(Graph<U>.Node node) {
        switch (direction) {
            case FORWARD:
                T newIn = applyMeet(node.pred().stream()
                        .map(outMap::get).collect(Collectors.toSet()));
                inMap.put(node, newIn);
                return newIn;
            case BACKWARD:
                T newOut = applyMeet(node.succ().stream()
                        .map(inMap::get).collect(Collectors.toSet()));
                outMap.put(node, newOut);
                return newOut;
            default:
                throw new IllegalAccessError("Weird direction in DFA");
        }
    }

    private T afterF(Graph<U>.Node node) {
        T lAfterF = F.apply(node, inputToF(node));
        switch (direction) {
            case FORWARD:
                outMap.put(node, lAfterF);
                return lAfterF;
            case BACKWARD:
                inMap.put(node, lAfterF);
                return lAfterF;
            default:
                throw new IllegalAccessError("Weird direction in DFA");
        }
    }

    public void runWorklistAlgo() {
        // already initialized to top

        // worklist <- { all nodes }
        // Maintaining a set and list to avoid concurrentModificationException
        // occurring when elts are removed/added to the set while iterating.
        // ListIterator on list avoids this problem, and set is needed to avoid
        // linear time contains lookups.
        Set<Graph<U>.Node> wlSet = new HashSet<>(graph.getAllNodes());
        Queue<Graph<U>.Node> wlQueue = new LinkedList<>();

        // get an initial ordering of the nodes
        // do a postorder traversal of the graph
        Stack<Graph<U>.Node> stack = graph.postOrderDFS();
        if (direction == Direction.FORWARD) {
            // reverse the ordering (make the order reverse post order)
            Stack<Graph<U>.Node> newStack = new Stack<>();
            while (!stack.isEmpty()) {
                newStack.push(stack.pop());
            }
            stack = newStack;
        }
        while (!stack.isEmpty())
            wlQueue.add(stack.pop());

        // Invariant: all nodes with unsatisfied eqns in worklist
        while (wlQueue.size() != 0) {
            Graph<U>.Node node = wlQueue.poll();// remove from queue
            wlSet.remove(node);// remove from set

            switch (direction) {
                case FORWARD:
                    T outBeforeUpdate = outMap.get(node);
                    T outAfterUpdate = afterF(node);
                    if (!outBeforeUpdate.equals(outAfterUpdate)) {
                        // out has changed, push succs of node to worklist
                        for (Graph<U>.Node sNode : node.succ()) {
                            if (!(wlSet.contains(sNode))) {
                                wlSet.add(sNode);
                                wlQueue.add(sNode);
                            }
                        }
                    }
                    break;
                case BACKWARD:
                    T inBeforeUpdate = inMap.get(node);
                    T inAfterUpdate = afterF(node);
                    if (!inBeforeUpdate.equals(inAfterUpdate)) {
                        // in has changed, push preds of node to worklist
                        for (Graph<U>.Node pNode : node.pred()) {
                            if (!(wlSet.contains(pNode))) {
                                wlSet.add(pNode);
                                wlQueue.add(pNode);
                            }
                        }
                    }
            }
        }
    }

    /**
     * Outputs the dot format of the graph to path with ins and outs
     * annotated in the graph.
     *
     * @param path path for writing the graph.
     */
    public void show(String path) throws IOException {
        Map<Graph<U>.Node, String> annotationsBefore = new HashMap<>();
        Map<Graph<U>.Node, String> annotationsAfter = new HashMap<>();
        for (Graph<U>.Node node : graph.getAllNodes()) {
            annotationsBefore.put(node, "in=" + inMap.get(node));
            annotationsAfter.put(node, "out=" + outMap.get(node));
        }
        graph.show(path, annotationsBefore, annotationsAfter);
    }
}
