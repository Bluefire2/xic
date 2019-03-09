package ast;

import symboltable.TypeSymTableFunc;
import java.util.Arrays;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class VisitorTranslationTest {
    private VisitorTranslation visitor;

    @Before
    public void setUp() {
        visitor = new VisitorTranslation();
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testFuncName0(){
        TypeSymTableFunc t = new TypeSymTableFunc(
                new TypeTTauArray(new TypeTTauArray(new TypeTTauInt())),
                new TypeTUnit());
        assertEquals(visitor.functionName("main",t), "_Imain_paai");
    }

    @Test
    public void testFuncName1(){
        TypeSymTableFunc t = new TypeSymTableFunc(
                new TypeTTauInt(),
                new TypeTTauArray(new TypeTTauInt()));
        assertEquals(visitor.functionName("unparseInt",t), "_IunparseInt_aii");
    }

    @Test
    public void testFuncName2(){
        TypeSymTableFunc t = new TypeSymTableFunc(
                new TypeTTauArray(new TypeTTauInt()),
                new TypeTList(
                        Arrays.asList(new TypeTTauInt(), new TypeTTauBool()))
        );
        assertEquals(visitor.functionName("parseInt",t), "_IparseInt_t2ibai");
    }

    @Test
    public void testFuncName3(){
        TypeSymTableFunc t = new TypeSymTableFunc(
                new TypeTUnit(),
                new TypeTTauBool());
        assertEquals(visitor.functionName("eof",t), "_Ieof_b");
    }

    @Test
    public void testFuncName4(){
        TypeSymTableFunc t = new TypeSymTableFunc(
                new TypeTList(
                        Arrays.asList(new TypeTTauInt(), new TypeTTauInt())),
                new TypeTTauInt());
        assertEquals(visitor.functionName("gcd",t), "_Igcd_iii");
    }

    @Test
    public void testFuncName5(){
        TypeSymTableFunc t = new TypeSymTableFunc(
                new TypeTUnit(),
                new TypeTUnit());
        assertEquals(visitor.functionName("multiple__underscores",t),
                "_Imultiple____underScores_p");
    }
}


