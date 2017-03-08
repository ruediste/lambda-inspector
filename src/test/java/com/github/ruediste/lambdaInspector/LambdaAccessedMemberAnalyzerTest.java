package com.github.ruediste.lambdaInspector;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import java.util.function.IntSupplier;
import java.util.function.Supplier;

import org.junit.Before;
import org.junit.Test;

import com.github.ruediste.lambdaInspector.Lambda.LambdaPropertyHandle;

public class LambdaAccessedMemberAnalyzerTest {

    String foo;

    int intVar;
    Integer integerVar;

    public String getBar() {
        return "bar";
    }

    private static class A {
        String foo = "foo";
        A a;

        public A getA(int index) {
            return a;
        }
    }

    A test;

    @Before
    public void before() {
        test = new A();
        test.a = new A();
        test.a.foo = "bar";

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
    public void testCastToInteger() {
        LambdaPropertyHandle inspect = LambdaInspector.inspect((Supplier<Integer>) () -> intVar).property;
        assertEquals("intVar", inspect.info.accessor.getName());
        assertSame(this, inspect.getBase());
    }

    @Test
    public void testCastToInt() {
        LambdaPropertyHandle inspect = LambdaInspector.inspect((IntSupplier) () -> integerVar).property;
        assertEquals("integerVar", inspect.info.accessor.getName());
        assertSame(this, inspect.getBase());
    }

    @Test
    public void testComplexBases() {
        assertSame(test.a, LambdaInspector.inspect((Supplier<String>) () -> test.a.foo).property.getBase());
        assertSame(test.a, LambdaInspector.inspect((Supplier<String>) () -> test.getA(7).foo).property.getBase());
    }
}
