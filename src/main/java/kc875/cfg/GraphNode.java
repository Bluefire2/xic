package kc875.cfg;

import java.util.HashSet;
import java.util.Set;

public class GraphNode {
    Set<GraphNode> in;
    Set<GraphNode> out;

    public GraphNode(Graph g, Set<GraphNode> in, Set<GraphNode> out) {
        this.in = in;
        this.out = out;
        g.addOtherNode(this);
    }

    public GraphNode(Graph g) {
        this(g, new HashSet<>(), new HashSet<>());
    }

    public Set<GraphNode> succ() {
        return new HashSet<>(out);
    }

    public Set<GraphNode> pred() {
        return new HashSet<>(in);
    }

    public Set<GraphNode> adj() {
        Set<GraphNode> neighbors = new HashSet<>(in);
        neighbors.addAll(new HashSet<>(out));
        return neighbors;
    }

    public int outDegree(){
        return out.size();
    }

    public int inDegree(){
        return in.size();
    }

    public int degree(){
        return in.size() + out.size();
    }

    public boolean goesTo(GraphNode n){
        return out.contains(n);
    }

    public boolean comesFrom(GraphNode n){
        return in.contains(n);
    }

    public boolean isAdj(GraphNode n){
        return in.contains(n) || out.contains(n);
    }
}
