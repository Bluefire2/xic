package kc875.cfg;

import java.util.HashSet;
import java.util.Set;

public class Graph {
    private GraphNode startNode;
    private Set<GraphNode> otherNodes;

    public Graph(GraphNode startNode, Set<GraphNode> otherNodes) {
        this.startNode = startNode;
        this.otherNodes = otherNodes;
    }

    public Graph() {
        this(null, new HashSet<>());
    }

    public Graph(GraphNode startNode) {
        this(startNode, new HashSet<>());
    }

    public GraphNode getStartNode() {
        return startNode;
    }

    public Set<GraphNode> getOtherNodes() {
        return otherNodes;
    }

    public void setStartNode(GraphNode startNode) {
        this.startNode = startNode;
    }

    public void addOtherNode(GraphNode otherNode) {
        this.otherNodes.add(otherNode);
    }

    public Set<GraphNode> getAllNodes() {
        Set<GraphNode> allNodes = new HashSet<>(otherNodes);
        allNodes.add(startNode);
        return allNodes;
    }

    public void addEdge(GraphNode from, GraphNode to) {
        from.out.add(to);
        to.in.add(from);
    }

    public void removeEdge(GraphNode from, GraphNode to) {
        from.out.remove(to);
        to.in.remove(from);
    }

    public void show() {
        //TODO this is for making .dot file
    }
}
