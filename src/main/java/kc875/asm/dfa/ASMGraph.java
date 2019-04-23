package kc875.asm.dfa;

import kc875.asm.ASMExprRegReplaceable;
import kc875.asm.ASMInstr;
import kc875.cfg.Graph;

import java.util.HashMap;
import java.util.List;

public class ASMGraph extends Graph<ASMInstr> {
    private HashMap<Node, List<ASMExprRegReplaceable>> genMap;
    private HashMap<Node, List<ASMExprRegReplaceable>> killMap;

    public List<ASMExprRegReplaceable> getUse(ASMInstr i) {
        //TODO
        return null;
    }

    public List<ASMExprRegReplaceable> getDef(ASMInstr i) {
        //TODO
        return null;
    }

    //constructor that returns a new flow graph
    public ASMGraph(List<ASMInstr> instrs) {
        this.genMap = new HashMap<>();
        this.killMap = new HashMap<>();

        for (ASMInstr ins : instrs) {
            Node node = new Node(ins);
            genMap.put(node, getUse(ins));
            killMap.put(node, getDef(ins));
            //TODO configure edges using inherited graph methods
        }
    }

    public ASMInstr instr(Node node) {
        return node.getT();
    }

    //USE - what temps are used at this node!
    public List<ASMExprRegReplaceable> gen(Node node) {
        return genMap.get(node);
    }

    //DEF - what temps are def'd at this node!
    public List<ASMExprRegReplaceable> kill(Node node) {
        return killMap.get(node);
    }

    public List<ASMExprRegReplaceable> use(Node node) {
        return gen(node);
    }

    public List<ASMExprRegReplaceable> def(Node node) {
        return kill(node);
    }
}
