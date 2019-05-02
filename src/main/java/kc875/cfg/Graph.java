package kc875.cfg;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
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

        Set<Node> succ() {
            return new HashSet<>(out);
        }

        Set<Node> pred() {
            return new HashSet<>(in);
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

    protected void setStartNode(Node startNode) {
        this.startNode = startNode;
    }

    protected void addOtherNode(Node otherNode) {
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
    protected void addEdge(Node from, Node to) {
        from.out.add(to);
        to.in.add(from);
    }

    /**
     * Does a post order traversal on the graph and returns the resulting
     * stack of nodes.
     */
    Stack<Node> postOrderDFS() {
        Stack<Node> s = new Stack<>();
        Set<Node> visited = new HashSet<>();

        // https://www.geeksforgeeks.org/iterative-postorder-traversal/
        Stack<Node> s1 = new Stack<>();
        Stack<Node> ordering = new Stack<>();

        if (startNode == null)
            return ordering;

        // push root to first stack
        s1.push(startNode);
        visited.add(startNode);

        // Run while first stack is not empty
        while (!s1.isEmpty()) {
            // Pop an item from s1 and push it to s2
            Node node = s1.pop();
            ordering.push(node);

            // Push children of removed item to s1
            for (Node succ : node.succ())
                if (!visited.contains(succ)) {
                    visited.add(succ);
                    s1.push(succ);
                }
        }

        return ordering;
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
    void show(String path,
              Map<Graph<T>.Node, String> annotationsBefore,
              Map<Graph<T>.Node, String> annotationsAfter)
            throws IOException {
        if (startNode == null) {
            throw new IllegalAccessError("No start node for the graph");
        }

        // Write prologue
        String INDENT_TAB = "\t";
        FileWriter f = new FileWriter(path);
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
