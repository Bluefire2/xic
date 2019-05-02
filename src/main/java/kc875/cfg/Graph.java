package kc875.cfg;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Sets;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringEscapeUtils;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * A Graph of nodes T.
 *
 * @param <T> Wrapped graph node (such as ASMInstr, IRStmt, List<ASMInstr> etc.).
 */
public class Graph<T> {
    private int numNodes = 0;

    public class Node {
        private T t;
        private Set<Node> in;
        private Set<Node> out;
        private int nodeID;

        public Node(T t, Set<Node> in, Set<Node> out) {
            this.nodeID = numNodes++;
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

        @Override
        public String toString() {
            String inNodes = "[" + in.stream()
                    .map(n -> "<" + n.getT().toString() + ">")
                    .collect(Collectors.joining(", ")) + "]";
            String outNodes = "[" + out.stream()
                    .map(n -> "<" + n.getT().toString() + ">")
                    .collect(Collectors.joining(", ")) + "]";
            return "Node{" +
                    "t=" + t +
                    ", in=" + inNodes +
                    ", out=" + outNodes +
                    '}';
        }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof Graph<?>.Node))
                return false;
            Graph<?>.Node onode = (Graph<?>.Node) o;
            return t.equals(onode.getT())
                    && in.equals(onode.in) && out.equals(onode.out);
        }

        @Override
        public int hashCode() {
            // Need to differentiate nodes with same contents but different
            // positions in the graph
            return nodeID + t.hashCode();
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
     * @param to   'receiver' node.
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
     * @param to   'receiver' node.
     */
    public void removeEdge(Node from, Node to) {
        from.out.remove(to);
        to.in.remove(from);
    }

    /**
     * Outputs the dot format of the graph to path. String annotations just
     * before and after nodes in the graph are obtained from
     * annotationsBefore and annotationsAfter respectively, if they exist.
     *
     * @param path              path for writing the graph.
     * @param annotationsBefore map of nodes to strings to be displayed
     *                          before each node.
     * @param annotationsAfter  map of nodes to strings to be displayed after
     *                          each node.
     */
    public void show(String path,
                     Map<Graph<T>.Node, String> annotationsBefore,
                     Map<Graph<T>.Node, String> annotationsAfter)
            throws IOException {
        if (startNode == null) {
            throw new IllegalAccessError("No start node for the graph");
        }

        // Write prologue
        String INDENT_TAB = "\t";
        FileWriter f = new FileWriter(path);
        String rawPath = FilenameUtils.getName(
                FilenameUtils.removeExtension(path)
        );
        f.write("digraph g {\n");
        f.write(INDENT_TAB + "node [shape=record];\n");
        f.write(INDENT_TAB + "forcelabels=true;\n");
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

                // label="..."
                String nodeLabel = node.getT().toString();
                // xlabel="<before...>\n<after...>"
                String nodeXLabel = annotationsBefore.getOrDefault(node, "")
                        + "\n"
                        + annotationsAfter.getOrDefault(node, "");
                f.write(
                        INDENT_TAB + id + INDENT_TAB + "["
                                + "label=\""
                                + StringEscapeUtils.escapeJava(nodeLabel)
                                + "\", "
                                + "xlabel=\""
                                + StringEscapeUtils.escapeJava(nodeXLabel)
                                + "\""
                                + "]\n"
                );
                i++;
            }
        }
        f.write("\n");

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

    /**
     * Outputs the dot format of the graph to path.
     *
     * @param path path for writing the graph.
     */
    public void show(String path) throws IOException {
        // Use the show without any annotations
        show(path, new HashMap<>(), new HashMap<>());
    }
}
