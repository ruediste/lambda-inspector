package com.github.ruediste.lambdaInspector;

import static org.junit.Assert.assertEquals;

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

    private Object inspect(Object lambda) {
        Expression expr = LambdaInspector.inspect(lambda).stat.expression;
        Object result = ExpressionEvaluator.evaluate(expr, this, new Object[] {}, new Object[] {});
        return result;
    }
}
