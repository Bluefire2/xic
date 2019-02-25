package symboltable;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

public class HashMapSymbolTable<T> implements SymbolTable<T> {
    // private so the caller can't mutate this
    private Stack<Map<String, T>> scopes;

    public HashMapSymbolTable() {
        this.scopes = new Stack<>();
    }

    @Override
    public T lookup(String id) throws NotFoundException {
        Stack<Map<String, T>> head = new Stack<>();
        T typeSymTable = null;

        while (!scopes.empty()) {
            Map<String, T> scope = scopes.peek();
            if (scope.containsKey(id)) {
                typeSymTable = scope.get(id);
                break;
            } else {
                // pop the current scope and try the previous one
                head.push(scopes.pop());
            }
        }

        // repair the scope stack
        while (!head.empty()) {
            scopes.push(head.pop());
        }

        if (typeSymTable != null) {
            return typeSymTable;
        } else {
            throw new NotFoundException(id);
        }
    }

    @Override
    public void add(String id, T typeSymTable) {
        if (scopes.empty()) {
            throw new IllegalStateException("Cannot add type: no scopes have been defined");
        }

        scopes.peek().put(id, typeSymTable); // TODO: should we throw if this ID already exists in the current scope?
    }

    @Override
    public void enterScope() {
        scopes.push(new HashMap<>());
    }

    @Override
    public void exitScope() {
        scopes.pop();
    }

    @Override
    public boolean contains(String id) {
        try {
            this.lookup(id);
            return true;
        } catch (NotFoundException e) {
            return false;
        }
    }

    /**
     * Private constructor for copying purposes.
     *
     * @param scopes The scopes to keep in the symbol table.
     */
    private HashMapSymbolTable(Stack<Map<String, T>> scopes) {
        this.scopes = scopes;
    }

    @Override
    public SymbolTable<T> copy() {
        Stack<Map<String, T>> scopesCopy = new Stack<>();
        // https://bugs.java.com/bugdatabase/view_bug.do?bug_id=4475301
        // the stack iterator iterates the "wrong" way!
        for (Map<String, T> scope : scopes) {
            // the keys/values aren't copied!!!
            Map<String, T> scopeCopy = new HashMap<>(scope);
            scopesCopy.push(scopeCopy);
        }

        return new HashMapSymbolTable<>(scopesCopy);
    }
}
