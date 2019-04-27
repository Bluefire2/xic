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

    public IRGraph(List<IRStmt> stmts){
        nodeMap = HashBiMap.create();

        List<IRStmt> basicBlocks = new ArrayList<>();
        HashMap<String, Integer> nodeLabelMap = new HashMap<>();
        HashMap<Integer, List<String>> jumps = new HashMap<>();


        IRSeq curr = new IRSeq();

            for (IRStmt s : stmts) {
                if (s instanceof IRLabel) {
                    basicBlocks.add(curr);
                    curr = new IRSeq();
                    curr.stmts().add(s);
                    nodeLabelMap.put(((IRLabel)s).name(), basicBlocks.size());
                }
                else curr.stmts().add(s);
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

        for (IRStmt bb : basicBlocks) {
            Graph<IRStmt>.Node n = new Graph<IRStmt>.Node(bb);
            if (getStartNode() == null) {
                setStartNode(n);
            }
            else addOtherNode(n);
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
     * Build the per-function control graph.
     * @param irnode The root IR node of the function declaration
     * @return an IRGraph, with basic blocks as nodes and jumps as edges.
     */
    public static IRGraph buildCFG(IRFuncDecl irnode) {
        List<IRStmt> stmts = new ArrayList<>();
        IRStmt topstmt = irnode.body();
        if (topstmt instanceof IRSeq) {
            stmts.addAll(((IRSeq) topstmt).stmts());
        }
        else stmts.add(topstmt);

        return new IRGraph(stmts);
    }

    /**
     * Flatten control flow graph back into IR
     * @param irGraph
     * @return an IR statement (IRSeq) constructed from irGraph
     */
    public static IRStmt flattenCFG(IRGraph irGraph) {
        IRSeq retseq = new IRSeq();
        for (Graph.Node n : irGraph.getAllNodes()) {
            IRStmt s = irGraph.getStmt(n);
            if (s instanceof IRSeq) {
                retseq.stmts().addAll(((IRSeq) s).stmts());
            }
            else retseq.stmts().add(s);
        }
        return retseq;
    }

    public IRStmt getStmt(Node n) { return nodeMap.get(n); }

    public void setStmt(Node n, IRStmt s) { nodeMap.replace(n,s); }




}
