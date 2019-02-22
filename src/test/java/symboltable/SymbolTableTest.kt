package symboltable

import org.junit.After
import org.junit.Before

import org.junit.Assert.*
import org.junit.Test

abstract class SymbolTableTest {
    abstract fun createInstance(): SymbolTable

    @Test(expected = NotFoundException::class)
    fun simpleTest() {
        val table = createInstance()
        table.lookup("a") // should throw an exception
    }

    @Before
    @Throws(Exception::class)
    fun setUp() {
    }

    @After
    @Throws(Exception::class)
    fun tearDown() {
    }
}