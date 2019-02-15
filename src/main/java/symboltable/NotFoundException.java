package symboltable;

public class NotFoundException extends Exception {
    private String message;

    public NotFoundException(String id) {
        this.message = String.format("Undefined identifier \"%s\"", id);
    }

    @Override
    public String getMessage() {
        return this.message;
    }
}
