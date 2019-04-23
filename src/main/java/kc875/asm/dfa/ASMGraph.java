package kc875.asm.dfa;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import kc875.asm.*;
import kc875.cfg.Graph;

import java.util.*;

public class ASMGraph extends Graph<ASMInstr> {
    private BiMap<Node, ASMInstr> nodeInstrMap;

    private static Set<ASMOpCode> noFallthrough = new HashSet<>(
            Arrays.asList(ASMOpCode.JMP, ASMOpCode.RET) // TODO
    );

    private static Set<ASMOpCode> jumps = new HashSet<>(
            Arrays.asList(
                    ASMOpCode.JMP,
                    ASMOpCode.JE,
                    ASMOpCode.JG,
                    ASMOpCode.JGE,
                    ASMOpCode.JL,
                    ASMOpCode.JLE,
                    ASMOpCode.JNE,
                    ASMOpCode.RET,
                    ASMOpCode.CALL
            ) // TODO
    );

    /**
     * Creates a Control-flow graph from the list of ASM instructions.
     *
     * @param instrs list of ASM instructions.
     */
    public ASMGraph(List<ASMInstr> instrs) {
        nodeInstrMap = HashBiMap.create();
        if (instrs.isEmpty()) return;

        HashMap<String, Node> labelToNodeMap = new HashMap<>();

        // FIRST PASS: add all instructions as nodes

        // set the start node
        Iterator<ASMInstr> iter = instrs.iterator();
        Node previous = new Node(iter.next());

        while (iter.hasNext()) {
            ASMInstr instr = iter.next();
            Node node = new Node(instr);
            this.addOtherNode(node);
            nodeInstrMap.put(node, instr);

            if (instr instanceof ASMInstrLabel) {
                labelToNodeMap.put(((ASMInstrLabel) instr).getName(), node);
            }

            // add an edge from the previous instruction's node if we need to
            if (hasEdgeToNextInstr(previous.getT())) {
                this.addEdge(previous, node);
            }
            previous = node;
        }

        // SECOND PASS: add CFG edges for jumps to labelled nodes

        for (Node node : this.getAllNodes()) {
            ASMInstr instr = node.getT();

            if (!jumps.contains(instr.getOpCode())) {
                // this instr is not a jump node, continue to next node
                continue;
            }

            // we need to add another edge to the jumped-to node!
            if (instr instanceof ASMInstr_1Arg) {
                // TODO: change for A7 since we'll be able to jump to non-labels
                ASMExprName label = (ASMExprName) ((ASMInstr_1Arg) instr).getArg();

                // get the node we jump to and add an edge to it
                Node to = labelToNodeMap.get(label.getName());
                this.addEdge(node, to);
            } else {
                // TODO: wtf
            }
        }
    }

    public BiMap<Node, ASMInstr> getNodeInstrMap() {
        return nodeInstrMap;
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
    private boolean hasEdgeToNextInstr(ASMInstr i) {
        return !noFallthrough.contains(i.getOpCode());
    }

}
