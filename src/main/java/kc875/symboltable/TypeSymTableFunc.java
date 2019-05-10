package kc875.symboltable;

import kc875.ast.TypeT;

public class TypeSymTableFunc extends TypeSymTable {
    private TypeT input;
    private TypeT output;
    //if func can be declared again w/ same sig (true for all interface functions)
    private boolean canDecl;

    public TypeSymTableFunc(TypeT input, TypeT output) {
        this.input = input;
        this.output = output;
        this.canDecl = true;
    }

    public TypeSymTableFunc(TypeT input, TypeT output, boolean canDecl) {
        this.input = input;
        this.output = output;
        this.canDecl = canDecl;
    }

    public TypeT getInput() {
        return input;
    }

    public TypeT getOutput() {
        return output;
    }

    public void set_can_decl(boolean can_decl) {
        this.canDecl = can_decl;
    }

    public boolean can_decl() {
        return canDecl;
    }
}
