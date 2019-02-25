package symboltable

class HashMapSymbolTableTest : SymbolTableTest() {
    override fun createInstance(): SymbolTable<String> {
        return HashMapSymbolTable()
    }
}