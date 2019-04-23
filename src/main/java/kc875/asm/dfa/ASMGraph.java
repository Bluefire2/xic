package kc875.asm.dfa;

import kc875.asm.ASMExprRegReplaceable;
import kc875.asm.ASMInstr;
import kc875.asm.ASMInstrLabel;
import kc875.asm.ASMOpCode;
import kc875.cfg.Graph;

import java.util.*;

public class ASMGraph extends Graph<ASMInstr> {
    private HashMap<Node, List<ASMExprRegReplaceable>> genMap;
    private HashMap<Node, List<ASMExprRegReplaceable>> killMap;
    private HashMap<ASMInstrLabel, Node> labels;

    private static Set<ASMOpCode> noFallthrough = new HashSet<>(
            Arrays.asList(ASMOpCode.JMP, ASMOpCode.RET) // TODO
    );

    public List<ASMExprRegReplaceable> getUse(ASMInstr i) {
        //TODO
        return null;
    }

    public List<ASMExprRegReplaceable> getDef(ASMInstr i) {
        //TODO
        return null;
    }

    /**
     * Check if an instruction needs a CFG edge to the instruction immediately
     * following it. This is false if the instruction is non-fallthrough, e.g.
     * an unconditional jump.
     *
     * @param i The instruction.
     * @return {@code true} if the instruction should have an edge to the next
     * instruction, {@code false} otherwise.
     */
    private boolean edgeToNextNode(ASMInstr i) {
        return !noFallthrough.contains(i.getOpCode());
    }

    //constructor that returns a new flow graph
    public ASMGraph(List<ASMInstr> instrs) {
        this.genMap = new HashMap<>();
        this.killMap = new HashMap<>();
        this.labels = new HashMap<>();

        if (instrs.isEmpty()) return;

        // FIRST PASS: add all instructions as nodes

        // set the start node
        Iterator<ASMInstr> iter = instrs.iterator();
        Node start = new Node(iter.next());
        this.setStartNode(start);

        Node previous = start;
        // if the previous instr was a label, this is non-null
        ASMInstrLabel previousLabel = null;

        while (iter.hasNext()) {
            ASMInstr instr = iter.next();
            if (instr.getOpCode() != ASMOpCode.LABEL) {
                Node node = new Node(instr);
                this.addOtherNode(node);

                // add an edge from the previous instruction's node if we need to
                if (edgeToNextNode(previous.getT())) {
                    this.addEdge(previous, node);
                }
                previous = node;

                // if the last instruction was a label, this node needs to be pointed to by that label
                if (previousLabel != null) {
                    labels.put(previousLabel, node);

                    // make sure to set this back to null
                    previousLabel = null;
                }
            } else {
                // don't make nodes for labels
                previousLabel = (ASMInstrLabel) instr;
            }
        }

        // TODO: second pass, add CFG edges for jumps to labelled nodes
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
