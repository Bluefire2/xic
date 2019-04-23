package kc875.cfg;

import com.google.common.collect.Sets;

import java.util.HashSet;
import java.util.Set;

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
        allNodes.add(startNode);
        return allNodes;
    }

    public void addEdge(Node from, Node to) {
        from.out.add(to);
        to.in.add(from);
    }

    public void removeEdge(Node from, Node to) {
        from.out.remove(to);
        to.in.remove(from);
    }

    public void show() {
        //TODO this is for making .dot file
    }
}
