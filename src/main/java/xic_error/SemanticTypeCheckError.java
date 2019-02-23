package xic_error;

import ast.TypeT;
import lexer.XiTokenLocation;

public class SemanticTypeCheckError extends SemanticError {
    private TypeT expected;
    private TypeT got;

    public SemanticTypeCheckError(TypeT expected, TypeT actual,
                                  XiTokenLocation location) {
        super(String.format("Expected %s, but found %s",
                expected.toString(), actual.toString()),
                location
        );
        this.expected = expected;
        this.got = actual;
    }

    public TypeT getExpectedType() {
        return expected;
    }

    public TypeT getActualType() {
        return got;
    }
}
