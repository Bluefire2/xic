package kc875.asm;

import kc875.asm.visit.ASMinstrBareVisitor;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.ArrayList;


public class ASMInstrDirective extends ASMInstr {
    String name;
    String data;

    public ASMInstrDirective(String name, String data) {
        super(ASMOpCode.DOT);
        this.name = name;
        this.data = data;
    }

    public ASMInstrDirective(String name) {
        super(ASMOpCode.DOT);
        this.name = name;
        this.data = "";
    }

    @Override
    public List<ASMInstr> accept(ASMinstrBareVisitor<List<ASMInstr>> v) {
        List<ASMInstr> l = new ArrayList<>();
        l.add(this);
        return l;
    }

    @Override
    public String toString() {
        //globl is not indented, the others are
        String s = (!name.equals("globl")) ? INDENT_TAB : "";
        return s + "." + name + ((!data.equals("")) ? " " + data : "");
    }

    @Override
    public boolean destIsDefButNoUse() {
        return false;
    }

    @Override
    public Set<ASMExprReg> implicitDefRegs() {
        return new HashSet<>();
    }

    @Override
    public Set<ASMExprReg> implicitUsedRegs() {
        return new HashSet<>();
    }

    @Override
    public boolean destHasNewDef() {
        return false;
    }
}
