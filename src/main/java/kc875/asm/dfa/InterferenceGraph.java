package kc875.asm.dfa;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import kc875.asm.ASMExprRT;
import kc875.asm.ASMInstr;
import kc875.cfg.Graph;

import java.util.List;
import java.util.HashSet;

public class InterferenceGraph extends Graph<ASMExprRT> {
    private BiMap<Node, ASMExprRT> nodeRegMap;

    public InterferenceGraph() {
        super(null, new HashSet<>());
        nodeRegMap = HashBiMap.create();
    }

    public Node getNode(ASMExprRT temp) {
        return nodeRegMap.inverse().get(temp);
    }
    //temp->node

    public ASMExprRT getTemp(Node node) {
        return nodeRegMap.get(node);
    }
    //node->temp

    //check if temp is in graph already
    public boolean checkTemp(ASMExprRT temp){
        return nodeRegMap.inverse().containsKey(temp);
    }

    public List<ASMInstr> moves(){
        //TODO
        return null;
    }

    public Node addNode(ASMExprRT t){
        Node n = new Node(t);
        nodeRegMap.put(n, t);
        addOtherNode(n);
        return n;
    }
    //which move instrs are associated with this graph

    public int spillCost(Node n) {
        return 1;//naive spilling
    }
}
