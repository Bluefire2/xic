package edu.cornell.cs.cs4120.xic.ir.visit;

import edu.cornell.cs.cs4120.xic.ir.IRNode;
import edu.cornell.cs.cs4120.xic.ir.IRTemp;

import java.util.ArrayList;
import java.util.List;

public class ListChildrenVisitor extends AggregateVisitor<List<IRNode>> {

    @Override
    public List<IRNode> unit() {
        return new ArrayList<IRNode>();
    }

    @Override
    public List<IRNode> bind(List<IRNode> r1, List<IRNode> r2) {
        ArrayList<IRNode> ret = new ArrayList<IRNode>();
        ret.addAll(r1);
        ret.addAll(r2);
        return ret;
    }

    @Override
    protected List<IRNode> leave(IRNode parent, IRNode n, List<IRNode> r,
                            AggregateVisitor<List<IRNode>> v_) {
        r.add(n);
        return r;
    }
}
