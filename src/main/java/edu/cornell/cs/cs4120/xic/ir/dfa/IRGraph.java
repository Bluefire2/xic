package edu.cornell.cs.cs4120.xic.ir.dfa;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import edu.cornell.cs.cs4120.xic.ir.*;
import kc875.cfg.Graph;
import kc875.utils.XiUtils;

import java.util.HashMap;
import java.util.Iterator;

public class IRGraph extends Graph<IRStmt> {

    // stmts are stored in order they appear in the function body, i.e., stmt
    // i (0-indexed) will have an entry node(stmt) <-> i.
    private BiMap<Node, Integer> nodeStmtMap;

    /**
     * Graph for lowered IR representation of a function.
     *
     * @param func function.
     */
    public IRGraph(IRFuncDecl func) {
        nodeStmtMap = HashBiMap.create();
        HashMap<String, Node> labelToNodeMap = new HashMap<>();

        // FIRST PASS: add all instructions as nodes
        IRStmt body = func.body();
        IRSeq stmts = body instanceof IRSeq ? (IRSeq) body : new IRSeq(body);
        if (stmts.stmts().isEmpty()) return;

        // set the start node
        Iterator<IRStmt> iter = stmts.stmts().iterator();
        IRStmt stmt = iter.next();
        Node previous = new Node(stmt);
        int nodeIdx = 0;
        nodeStmtMap.put(previous, nodeIdx++);
        setStartNode(previous);

        while (iter.hasNext()) {
            stmt = iter.next();
            if (stmt instanceof IRSeq && ((IRSeq) stmt).stmts().size() == 0) continue;
            Node node = new Node(stmt);
            addOtherNode(node);
            nodeStmtMap.put(node, nodeIdx++);

            if (stmt instanceof IRLabel) {
                labelToNodeMap.put(((IRLabel) stmt).name(), node);
            }

            // add an edge from the previous instruction's node if we need to
            if (!(previous.getT() instanceof IRJump
                    || previous.getT() instanceof IRReturn)) {
                // not an unconditional jump or return, add an edge
                addEdge(previous, node);
            }
            previous = node;
        }

        // SECOND PASS: add CFG edges for jumps to labelled nodes

        for (Node node : getAllNodes()) {
            stmt = node.getT();

            if (!(stmt instanceof IRJump || stmt instanceof IRCJump)) {
                // stmt is not a jump node, continue to next node
                continue;
            }

            // we need to add another edge to the jumped-to node
            if (stmt instanceof IRJump) {
                // TODO: change for A7 since we'll be able to jump to non-labels
                IRName arg = (IRName) ((IRJump) stmt).target();

                // If the arg is not for a function, then we can jump to it
                // inside this function
                // get the node we jump to and add an edge to it
                if (!XiUtils.isNonLibFunction(arg.name())) {
                    Node to = labelToNodeMap.get(arg.name());
                    addEdge(node, to);
                }
            } else {
                // stmt is CJUMP
                String trueLabel = ((IRCJump) stmt).trueLabel();

                if (!XiUtils.isNonLibFunction(trueLabel)) {
                    Node to = labelToNodeMap.get(trueLabel);
                    addEdge(node, to);
                }

                // May have false label
                if (((IRCJump) stmt).hasFalseLabel()) {
                    String falseLabel = ((IRCJump) stmt).falseLabel();
                    if (!XiUtils.isNonLibFunction(falseLabel)) {
                        Node to = labelToNodeMap.get(falseLabel);
                        addEdge(node, to);
                    }
                }
            }
        }
    }


    public int getStmt(Node n) {
        return nodeStmtMap.get(n);
    }

    public Node getNode(int i) {
        return nodeStmtMap.inverse().get(i);
    }

    public BiMap<Node, Integer> getNodeStmtMap() {
        return HashBiMap.create(nodeStmtMap);
    }

}
