package symboltable

class HashMapSymbolTableTest : SymbolTableTest() {
    override fun createInstance(): SymbolTable {
        return HashMapSymbolTable()
    }
}