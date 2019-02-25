package symboltable;

public interface SymbolTable<T> {
    /**
     * Look up the type of an identifier.
     *
     * @param id The identifier.
     * @return The type of the identifier, if it has been declared.
     * @throws NotFoundException If there is no identifier {@code id} in the
     *         current symTable.
     */
    T lookup(String id) throws NotFoundException;

    /**
     * Check if table contains an identifier.
     *
     * @param id The identifier.
     * @return true if the identifier is in the table, false otherwise.
     */
    boolean contains(String id);

    /**
     * Add an identifier to the current scope with a given type.
     * @param id The identifier.
     * @param type The type.
     */
    void add(String id, T type);

    /** Create and enter a new scope */
    void enterScope();

    /** Exit the current scope */
    void exitScope();
}
