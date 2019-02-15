package symboltable;

import ast.Type;

public interface SymbolTable {
    Type lookup(String ID) throws NotFoundException;
}
