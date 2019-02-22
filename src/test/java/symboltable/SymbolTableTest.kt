package symboltable

import ast.TypeTTauInt
import org.junit.After
import org.junit.Before

import org.junit.Assert.*
import org.junit.Test

abstract class SymbolTableTest {
    lateinit var table: SymbolTable

    abstract fun createInstance(): SymbolTable

    @Test(expected = NotFoundException::class)
    fun simpleTest() {
        table.lookup("a") // should throw an exception
    }

    @Test
    fun singleScope() {
        table.enterScope()
        table.add("x", TypeSymTableVar(TypeTTauInt()))
        val typeOfx = table.lookup("x")
        assertEquals(typeOfx, TypeSymTableVar(TypeTTauInt()))
    }

    private fun sameType(t1: TypeSymTable, t2: TypeSymTable): Boolean {
        if (t1 is TypeSymTableVar && t2 is TypeSymTableVar) {
            return t1.typeTTau == t2.typeTTau
        } else if ()
    }

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