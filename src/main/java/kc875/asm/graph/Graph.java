package kc875.asm.graph;
import java.util.List;

public class Graph {
    // TODO: a start node, and a list of other nodes
    List<GraphNode> nodes;

    // TODO: initialize nodes
    public Graph(){ }

    public List<GraphNode> getNodes() {
        return nodes;
    }

    public GraphNode newNode() {
        GraphNode node = new GraphNode(this);
        nodes.add(node);
        return node;
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
