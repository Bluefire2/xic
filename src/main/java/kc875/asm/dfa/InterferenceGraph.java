package kc875.asm.dfa;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import kc875.asm.ASMExprRegReplaceable;
import kc875.asm.ASMInstr;
import kc875.cfg.Graph;
import polyglot.util.Pair;

import java.util.List;
import java.util.Set;
import java.util.HashSet;

public class InterferenceGraph extends Graph<ASMExprRegReplaceable> {
    private BiMap<Node, ASMExprRegReplaceable> nodeRegMap;

    public InterferenceGraph() {
        super(null, new HashSet<>());
        nodeRegMap = HashBiMap.create();
    }

    public Node getNode(ASMExprRegReplaceable temp) {
        return nodeRegMap.inverse().get(temp);
    }
    //temp->node

    public ASMExprRegReplaceable getTemp(Node node) {
        return nodeRegMap.get(node);
    }
    //node->temp

    //check if temp is in graph already
    public boolean checkTemp(ASMExprRegReplaceable temp){
        return nodeRegMap.inverse().containsKey(temp);
    }

    public List<ASMInstr> moves(){
        //TODO
        return null;
    }

    public Node addNode(ASMExprRegReplaceable t){
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
