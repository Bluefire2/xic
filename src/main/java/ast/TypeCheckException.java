package ast;

public class TypeCheckException extends ASTException {
    private String message;
    private TypeT expected;
    private TypeT got;


    public TypeCheckException(TypeT expected, TypeT actual) {
        this.message = String.format("Expected %s, but found %s",
                expected.toString(), actual.toString());
        this.expected = expected;
        this.got = actual;
    }

    @Override
    public String getMessage() {
        return this.message;
    }

    public TypeT getExpectedType() {
        return expected;
    }

    public TypeT getActualType() {
        return got;
    }
}
