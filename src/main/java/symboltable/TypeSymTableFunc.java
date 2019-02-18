package symboltable;

import ast.TypeT;

public class TypeSymTableFunc extends TypeSymTable {
    private TypeT input;
    private TypeT output;

    public TypeSymTableFunc(TypeT input, TypeT output) {
        this.input = input;
        this.output = output;
    }

    public TypeT getInput() {
        return input;
    }

    public TypeT getOutput() {
        return output;
    }
}
