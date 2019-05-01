package kc875.asm;

import kc875.asm.visit.ASMinstrBareVisitor;
import kc875.utils.XiUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ASMInstr_1Arg extends ASMInstr {
    private ASMExpr arg;

    private static final List<String> paramRegs = List.of(
            "rdi", "rsi", "rdx", "rcx", "r8", "r9"
    );

    public ASMInstr_1Arg(ASMOpCode opCode, ASMExpr arg) {
        super(opCode);
        this.arg = arg;
    }

    @Override
    public String toString() {
        if (getOpCode() == ASMOpCode.RET)
            return INDENT_TAB + formatOpCode();
        else
            return INDENT_TAB + formatOpCode() + " " + arg.toString();
    }

    @Override
    public List<ASMInstr> accept(ASMinstrBareVisitor<List<ASMInstr>> v) {
        return v.visit(this);
    }

    public ASMExpr getArg() {
        return arg;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ASMInstr_1Arg) {
            ASMInstr_1Arg o = (ASMInstr_1Arg) obj;
            return this.getOpCode().equals(o.getOpCode())
                    && this.arg.equals(o.arg);
        }
        return false;
    }

    @Override
    public boolean destHasNewDef() {
        if (!(arg instanceof ASMExprRT)) {
            return false;
        }
        // arg is reg/temp
        switch (this.getOpCode()) {
            case POP:
            case SETE:
            case SETNE:
            case SETG:
            case SETGE:
            case SETL:
            case SETLE:
                return true;
            default:
                return false;
        }
    }

    @Override
    public Set<ASMExprReg> implicitDefRegs() {
        Set<ASMExprReg> s = new HashSet<>();
        switch (getOpCode()) {
            case IMUL:
            case IDIV:
                s.add(new ASMExprReg("rax"));
                s.add(new ASMExprReg("rdx"));
                break;
            case CALL:
                // all caller save regs defined
                s.add(new ASMExprReg("rax"));
                s.add(new ASMExprReg("rdx"));
                s.add(new ASMExprReg("rcx"));
                s.add(new ASMExprReg("rdi"));
                s.add(new ASMExprReg("rsi"));
                s.add(new ASMExprReg("r8"));
                s.add(new ASMExprReg("r9"));
                s.add(new ASMExprReg("r10"));
                s.add(new ASMExprReg("r11"));
                break;
            default:
                break;
        }
        return s;
    }

    @Override
    public Set<ASMExprReg> implicitUsedRegs() {
        Set<ASMExprReg> s = new HashSet<>();
        switch (getOpCode()) {
            case RET:
                if (!(arg instanceof ASMExprConst))
                    throw new IllegalAccessError("RET should have a const");
                long nRets = ((ASMExprConst) arg).getVal();
                if (nRets >= 2)
                    s.add(new ASMExprReg("rdx"));
                // always add rax (ABI spec)
                s.add(new ASMExprReg("rax"));
                return s;
            case IMUL:
            case IDIV:
                s.add(new ASMExprReg("rax"));
                s.add(new ASMExprReg("rdx"));
                return s;
            case CALL:
                if (!(arg instanceof ASMExprName))
                    return s;
                String name = ((ASMExprName) arg).getName();
                if (name.equals("_xi_out_of_bounds")) {
                    // no parameters used
                    return s;
                } else if (name.equals("_xi_alloc")) {
                    // one parameter used
                    s.addAll(paramRegs.subList(0, 1).stream()
                            .map(ASMExprReg::new)
                            .collect(Collectors.toSet()));
                    return s;
                } else if (XiUtils.isNonLibFunction(name)) {
                    int nParams = ASMUtils.getNumParams(name);
                    if (ASMUtils.getNumReturns(name) > 2)
                        // more than 2 rets, extra parameter passed to func
                        nParams++;
                    s.addAll(paramRegs.subList(0, nParams > 6 ? 6 : nParams)
                            .stream().map(ASMExprReg::new)
                            .collect(Collectors.toSet()));
                    return s;
                }
                break;
            default:
                break;
        }
        return s;
    }

    @Override
    public boolean destIsDefButNoUse() {
        if (!(arg instanceof ASMExprRT)) {
            return false;
        }
        // arg is reg/temp
        switch (this.getOpCode()) {
            case POP:
            case SETE:
            case SETNE:
            case SETG:
            case SETGE:
            case SETL:
            case SETLE:
                return true;
            default:
                return false;
        }
    }
}
