package symboltable;

import ast.Type;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

public class HashMapSymbolTable implements SymbolTable {
    // private so the caller can't mutate this
    private Stack<Map<String, Type>> scopes;

    public HashMapSymbolTable() {
        this.scopes = new Stack<>();
    }

    @Override
    public Type lookup(String id) throws NotFoundException {
        Stack<Map<String, Type>> head = new Stack<>();
        Type type = null;

        while (!scopes.empty()) {
            Map<String, Type> scope = scopes.peek();
            if (scope.containsKey(id)) {
                type = scope.get(id);
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

        if (type != null) {
            return type;
        } else {
            throw new NotFoundException(id);
        }
    }

    @Override
    public void add(String id, Type type) {
        if (scopes.empty()) {
            throw new IllegalStateException("Cannot add type: no scopes have been defined");
        }

        scopes.peek().put(id, type); // TODO: should we throw if this ID already exists in the current scope?
    }

    @Override
    public void enterScope() {
        scopes.push(new HashMap<>());
    }

    @Override
    public void exitScope() {
        scopes.pop();
    }
}
