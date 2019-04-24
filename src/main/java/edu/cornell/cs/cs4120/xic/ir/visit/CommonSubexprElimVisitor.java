package edu.cornell.cs.cs4120.xic.ir.visit;

import edu.cornell.cs.cs4120.xic.ir.*;
import edu.cornell.cs.cs4120.xic.ir.dfa.AvailableExprsDFA;
import edu.cornell.cs.cs4120.xic.ir.dfa.IRGraph;
import kc875.cfg.Graph;

import java.util.*;

public class CommonSubexprElimVisitor {

    public CommonSubexprElimVisitor() { }

    private int tempcounter;
    public String newTemp() {
        return String.format("cse_t%d", tempcounter++);
    }

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

        return new IRGraph(stmts);
    }

    /**
     * Perform common subexpression elimination
     * @param irnode
     * @return irnode with common subexpressions hoisted and replaced by temps.
     */
    public IRCompUnit removeCommonSubExpressions(IRCompUnit irnode) {
        IRCompUnit optimizedCompUnit = new IRCompUnit(irnode.name());
        for (IRFuncDecl funcDecl : irnode.functions().values()) {
            IRGraph irGraph = buildCFG(funcDecl);
            AvailableExprsDFA availableExprsDFA = new AvailableExprsDFA(irGraph);
            availableExprsDFA.runWorklistAlgo();

            HashMap<IRExpr, String> tempExprMap = new HashMap<>();
            for (Graph<IRStmt>.Node n : irGraph.getAllNodes()) {
                IRSeq seq = new IRSeq();
                for (IRExpr e : availableExprsDFA.exprsGeneratedBy(n)) {
                    String tmp = newTemp();
                    tempExprMap.put(e, tmp);
                    seq.stmts().add(new IRMove(new IRTemp(tmp), e));
                    IRStmt nodestmt = irGraph.getStmt(n);
                    if (nodestmt instanceof IRSeq) {
                        seq.stmts().addAll(((IRSeq) nodestmt).stmts());
                    }
                    else seq.stmts().add(nodestmt);
                }

                IRSeq currNodeStmts;
                IRStmt currs = irGraph.getStmt(n);
                if (currs instanceof IRSeq) {
                    currNodeStmts = (IRSeq) currs;
                }
                else currNodeStmts = new IRSeq(currs);

                for (IRStmt s : currNodeStmts.stmts()) {
                    //TODO: look up child exprs in tempExprMap and replace if possible
                }

                irGraph.setStmt(n, seq);

            }

            IRFuncDecl optimizedFuncDecl = new IRFuncDecl(funcDecl.name(),
                    flattenCFG((IRGraph) availableExprsDFA.getGraph()));
            optimizedCompUnit.functions().put(funcDecl.name(), optimizedFuncDecl);
        }
        return optimizedCompUnit;
    }

    /**
     * Flatten control flow graph back into IR
     * @param irGraph
     * @return an IR statement (IRSeq) constructed from irGraph
     */
    public IRStmt flattenCFG(IRGraph irGraph) {
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




}
