package kc875.symboltable;

public class HashMapSymbolTableTest extends SymbolTableTest {
    @Override
    HashMapSymbolTable<String> createInstance() {
        return new HashMapSymbolTable<>();
    }
}