package symboltable;

import ast.Type;

public interface SymbolTable {
    /**
     * Look up the type of an identifier.
     *
     * @param id The identifier.
     * @return The type of the identifier, if it has been declared.
     * @throws NotFoundException If there is no identifier {@code id} in the
     *         current context.
     */
    Type lookup(String id) throws NotFoundException;

    /**
     * Add an identifier to the current scope with a given type.
     * @param id The identifier.
     * @param type The type.
     */
    void add(String id, Type type);

    /** Create and enter a new scope */
    void enterScope();

    /** Exit the current scope */
    void exitScope();
}
