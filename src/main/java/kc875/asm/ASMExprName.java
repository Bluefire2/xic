package kc875.asm;

import kc875.utils.XiUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Wrapper for the name of the label. Used for control flow (jumping etc.)
 */
public class ASMExprName extends ASMExpr {
    private String name;

    private static final List<String> paramRegs = List.of(
            "rdi", "rsi", "rdx", "rcx", "r8", "r9"
    );

    public ASMExprName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ASMExprName) {
            return this.name.equals(((ASMExprName) obj).name);
        }
        return false;
    }

    @Override
    public Set<ASMExprRT> vars() {
        if (name.equals("_xi_out_of_bounds")) {
            // no parameters used
            return new HashSet<>();
        } else if (name.equals("_xi_alloc")) {
            // one parameter used
            return paramRegs.subList(0, 1).stream()
                    .map(ASMExprReg::new)
                    .collect(Collectors.toSet());
        } else if (XiUtils.isNonLibFunction(name)) {
            int nParams = ASMUtils.getNumParams(name);
            if (ASMUtils.getNumReturns(name) > 2)
                // more than 2 rets, extra parameter passed to func
                nParams++;
            return paramRegs.subList(0, nParams > 6 ? 6 : nParams).stream()
                    .map(ASMExprReg::new).collect(Collectors.toSet());
        } else {
            return new HashSet<>();
        }
    }
}
