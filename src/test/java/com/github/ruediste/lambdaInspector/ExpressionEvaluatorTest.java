package com.github.ruediste.lambdaInspector;

import static org.junit.Assert.assertEquals;

import java.util.function.Function;
import java.util.function.Supplier;

import org.junit.Before;
import org.junit.Test;

import com.github.ruediste.lambdaInspector.expr.Expression;

public class ExpressionEvaluatorTest {

    int foo;

    @Before
    public void before() {
        LambdaInspector.setup();
    }

    @Test
    public void testFieldAccess() {
        foo = 8;
        assertEquals(8, inspect((Supplier<Integer>) () -> foo));
    }

    @Test
    public void testConst() {
        assertEquals(7, inspect((Supplier<Integer>) () -> 7));
    }

    @Test
    public void testAdd() {
        foo = 7;
        assertEquals(8, inspect((Supplier<Integer>) () -> foo + 1));
    }

    @Test
    public void testNew() {
        assertEquals(11, inspect((Supplier<Integer>) () -> new Integer(9) + 2));
    }

    @Test
    public void testArg() {
        assertEquals(11, inspect((Function<Integer, Integer>) (x) -> x, 11));
    }

    @Test
    public void testCaptured() {
        int foo = 5;
        assertEquals(5, inspect((Supplier<Integer>) () -> foo));
    }

    @Test
    public void testNeg() {
        int foo = 5;
        assertEquals(-5, inspect((Supplier<Integer>) () -> -foo));
    }

    @Test
    public void testLongToDouble() {
        long foo = 5;
        assertEquals(5.0, inspect((Supplier<Double>) () -> (double) foo));
    }

    @Test
    public void testCast() {
        Supplier<String> sup = () -> "foo";
        assertEquals("foo", inspect((Supplier<String>) () -> sup.get()));
    }

    @Test
    public void testArrayLength() {
        assertEquals(1, inspect((Supplier<Integer>) () -> new String[] { "foo" }.length));
    }

    @Test
    public void testArrayLoad() {
        String[] array = new String[] { "foo" };
        assertEquals("foo", inspect((Supplier<String>) () -> array[0]));
    }

    @Test
    public void testInstanceOf() {
        Object obj = "";
        assertEquals(true, inspect((Supplier<Boolean>) () -> obj instanceof String));
    }

    private Object inspect(Object lambdaObj, Object... args) {
        Lambda lambda = LambdaInspector.inspect(lambdaObj);
        Expression expr = lambda.static_.expression;
        Object result = ExpressionEvaluator.evaluate(expr, lambda, args);
        return result;
    }
}
