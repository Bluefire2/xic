package edu.cornell.cs.cs4120.xic.ir.visit;

import edu.cornell.cs.cs4120.xic.ir.*;
import edu.cornell.cs.cs4120.xic.ir.dfa.AvailableExprsDFA;
import edu.cornell.cs.cs4120.xic.ir.dfa.IRGraph;
import kc875.cfg.Graph;
import kc875.utils.SetWithInf;

import java.util.*;

public class CommonSubexprElimVisitor {

    public CommonSubexprElimVisitor() { }

    /**
     * Build the per-function control graph.
     * @param irnode The root IR node of the function declaration
     * @return an IRGraph, with basic blocks as nodes and jumps as edges.
     */
    public IRGraph buildCFG(IRFuncDecl irnode) {
        List<IRStmt> stmts = new ArrayList<>();
        IRStmt topstmt = irnode.body();
        if (topstmt instanceof IRSeq) {
            stmts.addAll(((IRSeq) topstmt).stmts());
        }
        else stmts.add(topstmt);

        List<IRStmt> basicBlocks = new ArrayList<>();
        HashMap<String, Integer> nodeLabelMap = new HashMap<>();
        HashMap<Integer, List<String>> jumps = new HashMap<>();

        IRLabel start = new IRLabel(irnode.name());

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

        IRGraph irGraph = new IRGraph(basicBlocks);
        //TODO: add edges

        return irGraph;
    }

    public IRCompUnit removeCommonSubExpressions(IRCompUnit irnode) {
        for (IRFuncDecl funcDecl : irnode.functions().values()) {
            IRGraph irGraph = buildCFG(funcDecl);
            AvailableExprsDFA availableExprsDFA = new AvailableExprsDFA(irGraph);



        }
        //TODO
        return null;
    }




}
