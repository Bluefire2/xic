package edu.cornell.cs.cs4120.xic.ir.visit;

import edu.cornell.cs.cs4120.xic.ir.IRCompUnit;
import edu.cornell.cs.cs4120.xic.ir.IRNodeFactory_c;
import edu.cornell.cs.cs4120.xic.ir.graph.IRFlowGraph;
import edu.cornell.cs.cs4120.xic.ir.graph.Quadruple;
import edu.cornell.cs.cs4120.xic.ir.graph.QuadrupleVisitor;

import java.util.ArrayList;
import java.util.List;

public class CommonSubexprElimVisitor {

    public CommonSubexprElimVisitor() { }

    public IRCompUnit removeCommonSubExpressions(IRCompUnit irnode) {
        QuadrupleVisitor qv = new QuadrupleVisitor(new IRNodeFactory_c());
        List<Quadruple> qlist = qv.visit(irnode);
        List<Quadruple> simpl_qlist = new ArrayList<>();
        for (Quadruple q : qlist) {
            simpl_qlist.addAll(qv.simplify(q));
        }
        IRFlowGraph flowGraph = new IRFlowGraph(simpl_qlist);


        //TODO
        return null;

    }


}
