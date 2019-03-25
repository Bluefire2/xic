package ast;

import ast.visit.IRTranslationVisitor;
import edu.cornell.cs.cs4120.xic.ir.*;
import lexer.XiTokenLocation;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import symboltable.TypeSymTableFunc;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class IRTranslationVisitorTest {
    private IRTranslationVisitor visitor;
    private XiTokenLocation l;

    @Before
    public void setUp() {
        visitor = new IRTranslationVisitor(true, "test");
        l = new XiTokenLocation(0,0);
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testFuncName0() {
        TypeSymTableFunc t = new TypeSymTableFunc(
                new TypeTTauArray(new TypeTTauArray(new TypeTTauInt())),
                new TypeTUnit());
        assertEquals(visitor.functionName("main", t), "_Imain_paai");
    }

    @Test
    public void testFuncName1() {
        TypeSymTableFunc t = new TypeSymTableFunc(
                new TypeTTauInt(),
                new TypeTTauArray(new TypeTTauInt()));
        assertEquals(visitor.functionName("unparseInt", t), "_IunparseInt_aii");
    }

    @Test
    public void testFuncName2() {
        TypeSymTableFunc t = new TypeSymTableFunc(
                new TypeTTauArray(new TypeTTauInt()),
                new TypeTList(
                        Arrays.asList(new TypeTTauInt(), new TypeTTauBool()))
        );
        assertEquals(visitor.functionName("parseInt", t), "_IparseInt_t2ibai");
    }

    @Test
    public void testFuncName3() {
        TypeSymTableFunc t = new TypeSymTableFunc(
                new TypeTUnit(),
                new TypeTTauBool());
        assertEquals(visitor.functionName("eof", t), "_Ieof_b");
    }

    @Test
    public void testFuncName4() {
        TypeSymTableFunc t = new TypeSymTableFunc(
                new TypeTList(
                        Arrays.asList(new TypeTTauInt(), new TypeTTauInt())),
                new TypeTTauInt());
        assertEquals(visitor.functionName("gcd", t), "_Igcd_iii");
    }

    @Test
    public void testFuncName5() {
        TypeSymTableFunc t = new TypeSymTableFunc(
                new TypeTUnit(),
                new TypeTUnit());
        assertEquals(visitor.functionName("multiple__underScores", t),
                "_Imultiple____underScores_p");
    }

    @Test
    public void testFolding0() {
        Expr e = new ExprBinop(Binop.AND,
                new ExprBoolLiteral(true,l),
                new ExprBinop(Binop.OR,
                        new ExprBoolLiteral(false, l),
                        new ExprBoolLiteral(true, l),
                        l),
                l);
        IRNode r = e.accept(visitor);
        assertTrue(r instanceof IRConst);
        assertEquals(((IRConst) r).value(), 1);
    }

    @Test //and/or
    public void testFolding1() {
        Expr e = new ExprBinop(Binop.AND,
                new ExprBoolLiteral(true,l),
                new ExprBinop(Binop.OR,
                        new ExprBoolLiteral(false, l),
                        new ExprBoolLiteral(false, l),
                        l),
                l);
        IRNode r = e.accept(visitor);
        assertTrue(r instanceof IRConst);
        assertEquals(((IRConst) r).value(), 0);
    }

    @Test //unary not
    public void testFolding2() {
        Expr e = new ExprUnop(Unop.NOT,
                new ExprBinop(Binop.GT,
                        new ExprIntLiteral(10L, l),
                        new ExprIntLiteral(10L, l),
                        l),
                l);
        IRNode r = e.accept(visitor);
        assertTrue(r instanceof IRConst);
        assertEquals(((IRConst) r).value(), 1);
    }

    @Test //unary not #2
    public void testFolding3() {
        Expr e = new ExprUnop(Unop.NOT,
                new ExprBinop(Binop.GTEQ,
                        new ExprIntLiteral(10L, l),
                        new ExprIntLiteral(10L, l),
                        l),
                l);
        IRNode r = e.accept(visitor);
        assertTrue(r instanceof IRConst);
        assertEquals(((IRConst) r).value(), 0);
    }

    @Test //arithmetic
    public void testFolding4() {
        Expr e = new ExprBinop(Binop.PLUS,
                new ExprBinop(Binop.PLUS,
                        new ExprIntLiteral(1L, l),
                        new ExprIntLiteral(2L, l),
                        l),
                new ExprBinop(Binop.PLUS,
                        new ExprIntLiteral(10L, l),
                        new ExprIntLiteral(10L, l),
                        l),
                l);
        IRNode r = e.accept(visitor);
        assertTrue(r instanceof IRConst);
        assertEquals(((IRConst) r).value(), 23L);
    }

    @Test //unary minus
    public void testFolding5() {
        Expr e = new ExprUnop(Unop.UMINUS,
                new ExprBinop(Binop.MULT,
                        new ExprIntLiteral(2L, l),
                        new ExprIntLiteral(2L, l),
                        l),
                l);
        IRNode r = e.accept(visitor);
        assertTrue(r instanceof IRConst);
        assertEquals(((IRConst) r).value(), -4L);
    }

    @Test //DbZ not folded
    public void testFolding6() {
        Expr e = new ExprBinop(Binop.DIV,
                        new ExprIntLiteral(1L, l),
                        new ExprIntLiteral(0L, l),
                        l);
        IRNode r = e.accept(visitor);
        assertTrue(r instanceof IRBinOp);
    }
}


