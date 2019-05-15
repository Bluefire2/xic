package kc875.asm;

import kc875.asm.visit.ASMinstrBareVisitor;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ASMInstr_1ArgCall extends ASMInstr_1Arg {
    private int numParams;
    private int numRets;

    private static final List<String> paramRegs = List.of(
            "rdi", "rsi", "rdx", "rcx", "r8", "r9"
    );

    public ASMInstr_1ArgCall(ASMExpr arg, int numParams, int numRets) {
        super(ASMOpCode.CALL, arg);
        this.numParams = numParams;
        this.numRets = numRets;
    }

    public int getNumParams() {
        return numParams;
    }

    public int getNumRets() {
        return numRets;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ASMInstr_1ArgCall) {
            ASMInstr_1ArgCall o = (ASMInstr_1ArgCall) obj;
            return this.getOpCode().equals(o.getOpCode())
                    && this.getArg().equals(o.getArg());
        }
        return false;
    }
    @Override
    public List<ASMInstr> accept(ASMinstrBareVisitor<List<ASMInstr>> v) {
        return v.visit(this);
    }

    @Override
    public boolean destHasNewDef() {
        return false;
    }

    @Override
    public Set<ASMExprReg> implicitDefRegs() {
        Set<ASMExprReg> s = new HashSet<>();
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
        return s;
    }

    @Override
    public Set<ASMExprReg> implicitUsedRegs() {
        int nParams = numParams;
        if (numRets > 2)
            // more than 2 rets, extra parameter passed to func
            nParams++;
        return paramRegs.subList(0, nParams > 6 ? 6 : nParams)
                .stream().map(ASMExprReg::new)
                .collect(Collectors.toSet());
    }

    @Override
    public boolean destIsDefButNoUse() {
        return false;
    }
}
