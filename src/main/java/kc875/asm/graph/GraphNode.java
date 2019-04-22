package kc875.asm.graph;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class GraphNode {
    Graph graph;
    HashSet<GraphNode> in;
    HashSet<GraphNode> out;

    // TODO: init in and out
    public GraphNode(Graph g){
        this.graph = g;
    }

    public List<GraphNode> succ() {
        return new ArrayList<>(out);
    }

    public List<GraphNode> pred() {
        return new ArrayList<>(in);
    }

    public List<GraphNode> adj() {
        List<GraphNode> neighbors = new ArrayList<>(in);
        neighbors.addAll(new ArrayList<>(out));
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
