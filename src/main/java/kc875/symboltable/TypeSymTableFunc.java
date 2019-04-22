package kc875.symboltable;

import kc875.ast.TypeT;

public class TypeSymTableFunc extends TypeSymTable {
    private TypeT input;
    private TypeT output;
    //if func can be declared again w/ same sig (true for all interface functions)
    private boolean can_decl;

    public TypeSymTableFunc(TypeT input, TypeT output) {
        this.input = input;
        this.output = output;
        this.can_decl = true;
    }

    public TypeSymTableFunc(TypeT input, TypeT output, boolean can_decl) {
        this.input = input;
        this.output = output;
        this.can_decl = can_decl;
    }

    public TypeT getInput() {
        return input;
    }

    public TypeT getOutput() {
        return output;
    }

    public void set_can_decl(boolean can_decl) {
        this.can_decl = can_decl;
    }

    public boolean can_decl() {
        return can_decl;
    }
}
