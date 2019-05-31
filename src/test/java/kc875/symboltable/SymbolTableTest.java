package kc875.symboltable;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public abstract class SymbolTableTest {
    private SymbolTable<String> table;

    abstract HashMapSymbolTable<String> createInstance();

    @Test(expected = NotFoundException.class)
    public void simpleTest() throws NotFoundException {
        table.lookup("a"); // should throw an exception
    }

    @Test
    public void singleScope() throws NotFoundException {
        table.enterScope();
        table.add("x", "xType");
        String typeOfx = table.lookup("x");
        assertEquals(typeOfx, "xType");
    }

    @Test
    public void multipleScopesShadowing() throws NotFoundException {
        table.enterScope();
        table.add("a", "A");
        table.enterScope();
        table.add("a", "B");

        assertEquals(table.lookup("a"), "B");
        table.exitScope();
        assertEquals(table.lookup("a"), "A");
    }

    @Before
    public void setUp() {
        table = createInstance();
    }

}