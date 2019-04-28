package edu.cornell.cs.cs4120.xic.ir.dfa;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import edu.cornell.cs.cs4120.xic.ir.*;
import kc875.cfg.Graph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class IRGraph extends Graph<IRStmt> {

    private BiMap<Node, IRStmt> nodeMap;

    private List<IRStmt> basicBlocks;
    private HashMap<String, Integer> nodeLabelMap;
    private HashMap<Integer, List<String>> jumps;

    private IRSeq curr;

    private void stmtsToBasicBlocks(IRStmt s) {
        if (s instanceof IRSeq) {
            for (IRStmt stmt : ((IRSeq) s).stmts()) {
                stmtsToBasicBlocks(stmt);
            }
        }
        else if (s instanceof IRLabel) {
            basicBlocks.add(curr);
            curr = new IRSeq();
            curr.stmts().add(s);
            nodeLabelMap.put(((IRLabel) s).name(), basicBlocks.size());
        } else curr.stmts().add(s);
        if (s instanceof IRJump ||
                s instanceof IRCJump ||
                s instanceof IRReturn) {
            basicBlocks.add(curr);
            curr = new IRSeq();
            List<String> blockjumps = new ArrayList<>();
            if (s instanceof IRCJump) {
                IRCJump sj = (IRCJump) s;
                blockjumps.add(sj.trueLabel());
                blockjumps.add(sj.falseLabel());
            }
            jumps.put(basicBlocks.size(), blockjumps);
        }

    }

    public IRGraph(IRFuncDecl node) {
        nodeMap = HashBiMap.create();
        basicBlocks = new ArrayList<>();
        nodeLabelMap = new HashMap<>();
        jumps = new HashMap<>();

        curr = new IRSeq();
        stmtsToBasicBlocks(node.body());

        for (IRStmt bb : basicBlocks) {
            Graph<IRStmt>.Node n = new Graph<IRStmt>.Node(bb);
            if (getStartNode() == null) {
                setStartNode(n);
            } else addOtherNode(n);
            nodeMap.put(n, bb);
        }

        for (Integer i : jumps.keySet()) {
            for (String s : jumps.get(i)) {
                if (nodeLabelMap.containsKey(s)) {
                    addEdge(nodeMap.inverse().get(basicBlocks.get(i)),
                            nodeMap.inverse().get(basicBlocks.get(nodeLabelMap.get(s))));
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
        return nodeMap.get(n);
    }

    public void setStmt(Node n, IRStmt s) {
        nodeMap.replace(n, s);
    }
}
