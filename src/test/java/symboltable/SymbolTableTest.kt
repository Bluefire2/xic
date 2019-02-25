package symboltable

import org.junit.After
import org.junit.Before

import org.junit.Assert.*
import org.junit.Test

abstract class SymbolTableTest {
    lateinit var table: SymbolTable<String>

    abstract fun createInstance(): SymbolTable<String>

    @Test(expected = NotFoundException::class)
    fun simpleTest() {
        table.lookup("a") // should throw an exception
    }

    @Test
    fun singleScope() {
        table.enterScope()
        table.add("x", "xType")
        val typeOfx = table.lookup("x")
        assertEquals(typeOfx, "xType")
    }

    @Test
    fun multipleScopesShadowing() {
        table.enterScope()
        table.add("a", "A")
        table.enterScope()
        table.add("a", "B")

        assertEquals(table.lookup("a"), "B")
        table.exitScope()
        assertEquals(table.lookup("a"), "A")
    }

    // TODO: add stress tests

    @Before
    @Throws(Exception::class)
    fun setUp() {
        table = createInstance()
    }

    @After
    @Throws(Exception::class)
    fun tearDown() {
    }
}