package symboltable;

import ast.TypeT;

public class TypeSymTableReturn extends TypeSymTable {
    private TypeT returnType;

    public TypeSymTableReturn(TypeT returnType) {
        this.returnType = returnType;
    }

    public TypeT getReturnType() {
        return returnType;
    }
}
