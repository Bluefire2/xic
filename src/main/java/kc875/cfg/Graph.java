package kc875.cfg;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Sets;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.Stack;
import java.util.stream.Collectors;

/**
 * A Graph of nodes T.
 * @param <T> Wrapped graph node (such as ASMInstr, IRStmt, List<ASMInstr> etc.).
 */
public class Graph<T> {
    public class Node {
        private T t;
        private Set<Node> in;
        private Set<Node> out;

        public Node(T t, Set<Node> in, Set<Node> out) {
            this.t = t;
            this.in = in;
            this.out = out;
        }

        public Node(T t) {
            this(t, new HashSet<>(), new HashSet<>());
        }

        public T getT() {
            return t;
        }

        public void addOut(Node out) {
            this.out.add(out);
        }

        public void addAllOut(Collection<? extends Node> c) {
            this.out.addAll(c);
        }

        public void addIn(Node in) {
            this.in.add(in);
        }

        public void addAllIn(Collection<? extends Node> c) {
            this.in.addAll(c);
        }

        public Set<Node> succ() {
            return new HashSet<>(out);
        }

        public Set<Node> pred() {
            return new HashSet<>(in);
        }

        public Set<Node> adj() {
            return Sets.union(in, out).immutableCopy();
        }

        public int outDegree() {
            return out.size();
        }

        public int inDegree() {
            return in.size();
        }

        public int degree() {
            return inDegree() + outDegree();
        }

        public boolean goesTo(Node n) {
            return out.contains(n);
        }

        public boolean comesFrom(Node n) {
            return in.contains(n);
        }

        public boolean isAdj(Node n) {
            return in.contains(n) || out.contains(n);
        }
    }

    private Node startNode;
    private Set<Node> otherNodes;

    public Graph(Node startNode, Set<Node> otherNodes) {
        this.startNode = startNode;
        this.otherNodes = otherNodes;
    }

    public Graph() {
        this(null, new HashSet<>());
    }

    public Graph(Node startNode) {
        this(startNode, new HashSet<>());
    }

    public Node getStartNode() {
        return startNode;
    }

    public Set<Node> getOtherNodes() {
        return otherNodes;
    }

    public void setStartNode(Node startNode) {
        this.startNode = startNode;
    }

    public void addOtherNode(Node otherNode) {
        this.otherNodes.add(otherNode);
    }

    public Set<Node> getAllNodes() {
        Set<Node> allNodes = new HashSet<>(otherNodes);
        if (startNode != null) {
            allNodes.add(startNode);
        }
        return allNodes;
    }

    /**
     * Adds an edge from node `from` to node `to`.
     *
     * @param from 'sender' node.
     * @param to 'receiver' node.
     */
    public void addEdge(Node from, Node to) {
        from.out.add(to);
        to.in.add(from);
    }

    /**
     * Removes an edge between node `from` and node `to`. Doesn't do anything
     * if the edge doesn't exist.
     *
     * @param from 'sender' node.
     * @param to 'receiver' node.
     */
    public void removeEdge(Node from, Node to) {
        from.out.remove(to);
        to.in.remove(from);
    }

    /**
     * Outputs the dot format of the graph to path.dot.
     *
     * @param path path for writing the graph.
     */
    public void show(String path) throws IOException {
        if (startNode == null) {
            throw new IllegalAccessError("No start node for the graph");
        }

        // Write prologue
        String INDENT_TAB = "    ";
        FileWriter f = new FileWriter(path + ".dot");
        f.write("digraph " + path + "\n");
        f.write(INDENT_TAB + "node [shape=record];\n");
        f.write("\n");

        // Assign IDs to all nodes and write them to file
        BiMap<Node, String> nodeIDMap = HashBiMap.create();
        int i = 0;
        for (Node node : getAllNodes()) {
            // if node already in map, don't assign a new ID
            if (!nodeIDMap.containsKey(node)) {
                // add this node to the map
                String id = "n_" + i;
                nodeIDMap.put(node, id);
                f.write(
                        INDENT_TAB + id + INDENT_TAB
                        + "[label=\"" + node.getT() + "\"]"
                        + "\n"
                );
                i++;
            }
        }
        f.write("\n");

        BiMap<String, Node> idNodeMap = nodeIDMap.inverse();

        Set<Node> visited = new HashSet<>();
        Stack<Node> unvisited = new Stack<>();
        unvisited.push(startNode);

        // add edges to the dot file
        while (!unvisited.isEmpty()) {
            // there is an unvisited node, visit it
            Node node = unvisited.pop();
            visited.add(node);

            // write edges from this node to successors to the file
            String succNodes = node.succ().stream()
                    .map(nodeIDMap::get)
                    .collect(Collectors.joining(", "));
            f.write(
                    INDENT_TAB
                    + nodeIDMap.get(node)
                    + INDENT_TAB + "->" + INDENT_TAB
                    + "{" + succNodes + "}"
                    + "\n"
            );

            for (Node succNode : node.succ()) {
                if (!visited.contains(succNode)) {
                    // succNode hasn't been visited, add to unvisited stack
                    unvisited.push(succNode);
                }
            }
        }

        f.write("\n}\n");
        f.close();
    }
}
