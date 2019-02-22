package ast;

public class SemanticErrorException extends ASTException {
    private String message;

    SemanticErrorException(String message, int line, int col) {
        this.message = line + ":" + col + " error: " + message;
    }

    public String getMessage() {
        return message;
    }

}
