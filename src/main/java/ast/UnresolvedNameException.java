package ast;

public class UnresolvedNameException extends ASTException {
    String message;

    public UnresolvedNameException(String name, int line, int col) {
        this.message = String.format(
                "%d:%d error: Name %s cannot be resolved",
                line,
                col,
                name
        );
    }

    public String getMessage() {
        return message;
    }
}
