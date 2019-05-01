package edu.cornell.cs.cs4120.xic.ir.dfa;

import com.google.common.collect.Sets;
import edu.cornell.cs.cs4120.xic.ir.*;
import edu.cornell.cs.cs4120.xic.ir.visit.ListChildrenVisitor;
import kc875.cfg.DFAFramework;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class LivenessDFA extends DFAFramework<Set<IRTemp>, IRStmt> {

    public LivenessDFA(IRGraph graph) {
        super(
                graph,
                Direction.BACKWARD,
                (node, l) -> new HashSet<>(Sets.union(use(node),
                        Sets.difference(l, def(node)))),
                HashSet::new,
                (l1, l2) -> new HashSet<>(Sets.union(l1, l2)),
                new HashSet<>()
        );
    }

    public static Set<IRTemp> def(IRGraph.Node node) {
        HashSet<IRTemp> defSet = new HashSet<>();
        IRStmt stmt = node.getT();
        List<IRStmt> stmts = new ArrayList<>();
        if (stmt instanceof IRSeq) {
            stmts.addAll(((IRSeq) stmt).stmts());
        }
        else stmts.add(stmt);

        for (IRStmt s : stmts) {
            if (s instanceof IRMove) {
                IRExpr target = ((IRMove) s).target();
                if (target instanceof IRTemp) {
                    defSet.add((IRTemp) target);
                }
            }
        }

        return defSet;
    }

    public static Set<IRTemp> use(IRGraph.Node node) {
        HashSet<IRTemp> useSet = new HashSet<>();
        IRStmt stmt = node.getT();
        List<IRStmt> stmts = new ArrayList<>();
        if (stmt instanceof IRSeq) {
            stmts.addAll(((IRSeq) stmt).stmts());
        }
        else stmts.add(stmt);

        for (IRStmt s : stmts) {
            if (s instanceof IRMove) {
                IRExpr target = ((IRMove) s).target();
                IRExpr source = ((IRMove) s).target();
                if (target instanceof IRTemp) {
                    useSet.addAll(getTempsUsedInExpr(source));
                }
                else if (target instanceof IRMem) {
                    useSet.addAll(getTempsUsedInExpr(target));
                    useSet.addAll(getTempsUsedInExpr(source));
                }
            }
            else if (s instanceof IRCJump) {
                IRExpr cond = ((IRCJump) s).cond();
                useSet.addAll(getTempsUsedInExpr(cond));
            }
            else if (s instanceof IRReturn) {
                for (IRExpr e : ((IRReturn) s).rets()) {
                    useSet.addAll(getTempsUsedInExpr(e));
                }
            }
        }

        return useSet;
    }

    public static Set<IRTemp> getTempsUsedInExpr(IRExpr expr) {
        ListChildrenVisitor lcv = new ListChildrenVisitor();
        List<IRNode> children = lcv.visit(expr);
        Set<IRTemp> tempSet = new HashSet<>();
            for (IRNode n : children) {
                if (n instanceof IRTemp) {
                        tempSet.add((IRTemp) n);
                    }
                }

        return tempSet;
    }

}
