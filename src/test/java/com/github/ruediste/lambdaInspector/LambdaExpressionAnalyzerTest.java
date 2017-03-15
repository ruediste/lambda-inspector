package com.github.ruediste.lambdaInspector;

import static org.junit.Assert.assertEquals;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import org.junit.BeforeClass;
import org.junit.Test;

import com.github.ruediste.lambdaInspector.expr.Expression;

public class LambdaExpressionAnalyzerTest {

    private Integer foo;

    private String bar() {
        return "bar";
    }

    @BeforeClass
    public static void beforeClass() {
        LambdaInspector.setup();
    }

    @Test
    public void testSimpe() {
        assertEquals("java.lang.System.out.println(\"Hello World\")", this.<Runnable>inspect(() -> {
            System.out.println("Hello World");
        }));
    }

    @Test
    public void testArg() {
        assertEquals("java.lang.System.out.println(arg$0)", this.<Consumer<String>>inspect(s -> {
            System.out.println(s);
        }));
    }

    @Test
    public void testCaptured() {
        String foo = "foo";

        assertEquals("java.lang.System.out.println(cap$0)", this.<Runnable>inspect(() -> {
            System.out.println(foo);
        }));
    }

    @Test
    public void testCapturedPrimitive() {
        int foo = 5;

        assertEquals("java.lang.System.out.println(cap$0)", this.<Runnable>inspect(() -> {
            System.out.println(foo);
        }));
    }

    @Test
    public void testConstantReturn() {
        assertEquals("\"foo\"", this.<Supplier<String>>inspect(() -> "foo"));
        assertEquals("java.lang.Integer.valueOf(1)", this.<Supplier<Integer>>inspect(() -> 1));
        assertEquals("java.lang.Long.valueOf(1)", this.<Supplier<Long>>inspect(() -> 1L));
    }

    @Test
    public void testArrayReturn() {
        assertEquals("new int[1]", this.<Supplier<int[]>>inspect(() -> {
            return new int[] { 2 };
        }));

    }

    @Test
    public void testControl() {
        assertEquals(null, this.<Supplier<Integer>>inspect(() -> {
            if (foo == 1)
                return 1;
            else
                return 2;
        }));
        assertEquals("<UNKNOWN>", this.<Supplier<Integer>>inspect(() -> {
            int tmp;
            if (foo == 1)
                tmp = 1;
            else
                tmp = 2;
            return tmp;
        }));
        assertEquals("<UNKNOWN>", this.<Supplier<String>>inspect(() -> {
            String tmp = "bar";
            while (foo == 4)
                tmp = "foo";
            return tmp;
        }));

    }

    @Test
    public void testThisReferences() {
        Integer i = 0;
        assertEquals("this.foo", this.<Supplier<Integer>>inspect(() -> foo));
        assertEquals("cap$0", this.<Supplier<Integer>>inspect(() -> {
            foo.byteValue();
            return i;
        }));
        assertEquals("cap$0", this.<Function<String, Integer>>inspect(s -> {
            foo.byteValue();
            return i;
        }));
        assertEquals("arg$0", this.<Function<String, String>>inspect(s -> {
            foo.byteValue();
            return s;
        }));
    }

    @Test
    public void createArrayType() throws ClassNotFoundException {
        assertEquals(String.class, Class.forName("[Ljava.lang.String;").getComponentType());
    }

    private void foo(String arg) {

    }

    @Test
    public void testMethodRef() {
        assertEquals("this.bar()", LambdaInspector.inspect((Supplier<String>) this::bar).static_.expression.toString());
        assertEquals("this.foo(arg$0)",
                LambdaInspector.inspect((Consumer<String>) this::foo).static_.expression.toString());
    }

    private static String barStatic() {
        return "bar";
    }

    private static void barStatic(String arg) {
    }

    @Test
    public void testMethodRefStatic() {
        assertEquals("com.github.ruediste.lambdaInspector.LambdaExpressionAnalyzerTest.barStatic()",
                LambdaInspector.inspect((Supplier<String>) LambdaExpressionAnalyzerTest::barStatic).static_.expression
                        .toString());
        assertEquals("com.github.ruediste.lambdaInspector.LambdaExpressionAnalyzerTest.barStatic(arg$0)",
                LambdaInspector.inspect((Consumer<String>) LambdaExpressionAnalyzerTest::barStatic).static_.expression
                        .toString());
    }

    private <T> String inspect(T lambda) {
        Expression expr = LambdaInspector.inspect(lambda).static_.expression;
        if (expr == null)
            return null;
        return expr.toString();
    }
}
