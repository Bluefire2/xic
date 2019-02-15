package ast;

import edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;
import polyglot.util.Pair;

public abstract class Stmt implements Printable {
    StmtType s_type;

    public StmtType getS_type() {
        return s_type;
    }

    public void printPair(Pair<String, Type> p, CodeWriterSExpPrinter w){
        if (p.part2().getT_type() != TypeType.AnyType){
            w.startList();
            w.printAtom(p.part1());
            p.part2().prettyPrint(w);
            w.endList();
        } else {
            w.printAtom("_");
        }
    }
}
