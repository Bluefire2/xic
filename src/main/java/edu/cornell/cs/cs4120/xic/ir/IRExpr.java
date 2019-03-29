package edu.cornell.cs.cs4120.xic.ir;

import asm.ASMExprTemp;
import asm.ASMInstr;
import edu.cornell.cs.cs4120.xic.ir.visit.ASMTranslationVisitor;

import java.util.List;
import java.util.function.Function;

public interface IRExpr extends IRNode {
    boolean isConstant();

    long constant();

    List<ASMInstr> accept(ASMTranslationVisitor v, ASMExprTemp t);

    /**
     * A matcher for IR expressions that can be nested inside an IR
     * instruction in the lowered IR. There's no function argument for
     * converting IRESeq since IRESeq objects can't be in lowered IR.
     *
     * Source: http://blog.higher-order.com/blog/2009/08/21/structural-pattern-matching-in-java/
     *
     * @param <T> Type of the value to be returned from matching.
     * @param a function from IRBinop to T.
     * @param b function from IRCall to T.
     * @param c function from IRConst to T.
     * @param d function from IRMem to T.
     * @param e function from IRName to T.
     * @param f function from IRTemp to T.
     * @return computed value T, the result of matching.
     */
    <T> T matchLow(Function<IRBinOp, T> a,
                   Function<IRCall, T> b,
                   Function<IRConst, T> c,
                   Function<IRMem, T> d,
                   Function<IRName, T> e,
                   Function<IRTemp, T> f);
}
