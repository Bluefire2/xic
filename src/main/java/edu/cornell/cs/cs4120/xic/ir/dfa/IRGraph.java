package edu.cornell.cs.cs4120.xic.ir.dfa;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import edu.cornell.cs.cs4120.xic.ir.*;
import kc875.cfg.Graph;
import kc875.utils.XiUtils;

import java.util.HashMap;
import java.util.Iterator;

public class IRGraph extends Graph<IRStmt> {

    private BiMap<Node, IRStmt> nodeStmtMap;

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
        nodeStmtMap.put(previous, stmt);
        setStartNode(previous);

        while (iter.hasNext()) {
            stmt = iter.next();
            Node node = new Node(stmt);
            addOtherNode(node);
            nodeStmtMap.put(node, stmt);

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
                if (!XiUtils.isFunction(arg.name())) {
                    Node to = labelToNodeMap.get(arg.name());
                    addEdge(node, to);
                }
            } else {
                // stmt is CJUMP
                String trueLabel = ((IRCJump) stmt).trueLabel();

                if (!XiUtils.isFunction(trueLabel)) {
                    Node to = labelToNodeMap.get(trueLabel);
                    addEdge(node, to);
                }

                // May have false label
                if (((IRCJump) stmt).hasFalseLabel()) {
                    String falseLabel = ((IRCJump) stmt).falseLabel();
                    if (!XiUtils.isFunction(falseLabel)) {
                        Node to = labelToNodeMap.get(falseLabel);
                        addEdge(node, to);
                    }
                }
            }
        }
    }

    /**
     * Flatten control flow graph back into IR
     *
     * @param irGraph graph to be flattened.
     * @return an IR statement (IRSeq) constructed from irGraph
     */
    public static IRStmt flattenCFG(IRGraph irGraph) {
        IRSeq retseq = new IRSeq();
        for (Graph<IRStmt>.Node n : irGraph.getAllNodes()) {
            IRStmt s = irGraph.getStmt(n);
            if (s instanceof IRSeq) {
                retseq.stmts().addAll(((IRSeq) s).stmts());
            } else retseq.stmts().add(s);
        }
        return retseq;
    }

    public IRStmt getStmt(Node n) {
        return nodeStmtMap.get(n);
    }

    public Node getNode(IRStmt stmt) {
        return nodeStmtMap.inverse().get(stmt);
    }

    public BiMap<Node, IRStmt> getNodeStmtMap() {
        return HashBiMap.create(nodeStmtMap);
    }

    public void setStmt(Node n, IRStmt s) {
        nodeStmtMap.replace(n, s);
    }
}
