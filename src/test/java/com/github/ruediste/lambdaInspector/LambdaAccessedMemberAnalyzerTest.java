package com.github.ruediste.lambdaInspector;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import java.util.function.IntSupplier;
import java.util.function.Supplier;

import org.junit.Before;
import org.junit.Test;

import com.github.ruediste.lambdaInspector.Lambda.LambdaAccessedMemberHandle;

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
        assertEquals("foo", inspect(() -> foo).getInfo().member.getName());
        assertEquals("getBar", inspect(() -> getBar()).getInfo().member.getName());
    }

    private LambdaAccessedMemberHandle inspect(Supplier<String> lambda) {
        return LambdaInspector.inspect(lambda).memberHandle;
    }

    @Test
    public void testBase() {
        assertSame(this, inspect(() -> foo).getBase());
        assertSame(this, inspect(() -> getBar()).getBase());
    }

    @Test
    public void testNested() {
        LambdaAccessedMemberHandle inspect = inspect(() -> test.foo);
        assertEquals("foo", inspect.getInfo().member.getName());
        assertSame(test, inspect.getBase());
    }

    @Test
    public void testCastToInteger() {
        LambdaAccessedMemberHandle inspect = LambdaInspector.inspect((Supplier<Integer>) () -> intVar).memberHandle;
        assertEquals("intVar", inspect.getInfo().member.getName());
        assertSame(this, inspect.getBase());
    }

    @Test
    public void testCastToInt() {
        LambdaAccessedMemberHandle inspect = LambdaInspector.inspect((IntSupplier) () -> integerVar).memberHandle;
        assertEquals("integerVar", inspect.getInfo().member.getName());
        assertSame(this, inspect.getBase());
    }

    @Test
    public void testComplexBases() {
        assertSame(test.a, LambdaInspector.inspect((Supplier<String>) () -> test.a.foo).memberHandle.getBase());
        assertSame(test.a, LambdaInspector.inspect((Supplier<String>) () -> test.getA(7).foo).memberHandle.getBase());
    }
}
