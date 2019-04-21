package edu.cornell.cs.cs4120.xic.ir.graph;

import edu.cornell.cs.cs4120.xic.ir.IRCompUnit;
import edu.cornell.cs.cs4120.xic.ir.IRStmt;

import java.util.List;

public class Quadruple  {

    IRStmt stmt;

    public Quadruple(IRStmt stmt) {
        this.stmt = stmt;
    }

    public boolean isSimplified() {
        //TODO
        return false;
    }

}
