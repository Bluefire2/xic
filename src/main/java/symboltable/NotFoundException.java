package symboltable;

public class NotFoundException extends Exception {
    private String message;

    public NotFoundException(String ID) {
        this.message = String.format("Undefined identified \"%s\"", ID);
    }

    @Override
    public String getMessage() {
        return this.message;
    }
}
