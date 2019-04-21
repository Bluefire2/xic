package edu.cornell.cs.cs4120.xic.ir.graph;

import edu.cornell.cs.cs4120.xic.ir.IRCompUnit;
import edu.cornell.cs.cs4120.xic.ir.IRNodeFactory;
import edu.cornell.cs.cs4120.xic.ir.visit.IRVisitor;

import java.util.List;

public class QuadrupleVisitor extends IRVisitor {

    public QuadrupleVisitor(IRNodeFactory inf) {
        super(inf);
    }

    public List<Quadruple> simplify(Quadruple q) {
        //TODO
        return null;
    }

    public List<Quadruple> visit(IRCompUnit irnode) {
        //TODO
        return null;
    }

}
