package asm.graph;
import java.util.List;

public class Graph {
    List<GraphNode> nodes;

    public Graph(){
        //TODO
    }

    public List<GraphNode> getNodes() {
        return nodes;
    }

    public GraphNode newNode() {
        return new GraphNode(this);
    }

    public void addEdge(GraphNode from, GraphNode to){
        from.out.add(to);
        to.in.add(from);
    }

    public void rmEdge(GraphNode from, GraphNode to){
        from.out.remove(to);
        to.in.remove(from);
    }

    public void show(){
        //TODO this is for making .dot file
    }

}
