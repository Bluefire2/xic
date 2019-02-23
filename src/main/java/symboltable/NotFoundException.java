package symboltable;

public class NotFoundException extends Exception {
    NotFoundException(String id) {
        super(String.format("Undefined identifier \"%s\"", id));
    }
}
