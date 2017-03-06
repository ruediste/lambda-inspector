package com.github.ruediste.lambdaInspector;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import java.util.function.Supplier;

import org.junit.Before;
import org.junit.Test;

import com.github.ruediste.lambdaInspector.Lambda.LambdaPropertyHandle;

public class LambdaPropertyAnalyzerTest {

    String foo;

    int intVar;

    public String getBar() {
        return "bar";
    }

    private static class A {
        String foo = "foo";
    }

    A test = new A();

    @Before
    public void before() {
        LambdaInspector.setup();
    }

    @Test
    public void testSimple() {
        assertEquals("foo", inspect(() -> foo).info.accessor.getName());
        assertEquals("getBar", inspect(() -> getBar()).info.accessor.getName());
    }

    private LambdaPropertyHandle inspect(Supplier<String> lambda) {
        return LambdaInspector.inspect(lambda).property;
    }

    @Test
    public void testBase() {
        assertSame(this, inspect(() -> foo).getBase());
        assertSame(this, inspect(() -> getBar()).getBase());
    }

    @Test
    public void testNested() {
        LambdaPropertyHandle inspect = inspect(() -> test.foo);
        assertEquals("foo", inspect.info.accessor.getName());
        assertSame(test, inspect.getBase());
    }

    @Test
    public void testCastToInt() {
        LambdaPropertyHandle inspect = LambdaInspector.inspect((Supplier<Integer>) () -> intVar).property;
        assertEquals("intVar", inspect.info.accessor.getName());
        assertSame(this, inspect.getBase());
    }
}
