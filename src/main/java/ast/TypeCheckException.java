package ast;

public class TypeCheckException extends Exception{
    private String message;
    private MetaType expected;
    private MetaType got;


    public TypeCheckException(MetaType expected, MetaType actual) {
        this.message = String.format("Expected %s, but found %s",
                expected.toString(), actual.toString());
        this.expected = expected;
        this.got = actual;
    }

    @Override
    public String getMessage() {
        return this.message;
    }

    public MetaType getExpectedType() {
        return expected;
    }

    public MetaType getActualType() {
        return got;
    }
}
