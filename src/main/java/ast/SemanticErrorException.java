package ast;

public class SemanticErrorException extends RuntimeException{
    String message;

    public SemanticErrorException(String message, int line, int col) {
        this.message = line + ":" + col + " error: " + message;
    }

    public String getMessage() {
        return message;
    }

}