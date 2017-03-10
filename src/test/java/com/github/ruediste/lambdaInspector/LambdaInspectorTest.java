package com.github.ruediste.lambdaInspector;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.lang.reflect.Type;
import java.util.function.Consumer;
import java.util.function.Supplier;

import org.junit.Before;
import org.junit.Test;

public class LambdaInspectorTest {

    String str = "Hello World";

    @Before
    public void before() {

        LambdaInspector.setup();
    }

    @Test
    public void testInspector() throws Exception {
        Runnable run = () -> {
        };
        LambdaInformation info = run.getClass().getAnnotation(LambdaInformation.class);
        assertEquals(LambdaInspectorTest.class, info.implMethodClass());
        assertEquals("lambda$0", info.implMethodName());
        assertEquals("()V", info.implMethodDesc());
        assertEquals(Runnable.class, info.samClass());
        assertEquals("run", info.samMethodName());
        assertEquals("()V", info.samMethodDesc());
    }

    private Lambda inspectStringConsumer(Consumer<String> lambda) {
        return LambdaInspector.inspect(lambda);
    }

    private Lambda inspectRunnable(Runnable lambda) {
        return LambdaInspector.inspect(lambda);
    }

    @Test
    public void testInspect() throws Exception {

        Lambda lambda = inspectRunnable(() -> {
        });
        assertEquals(null, lambda.this_);
        assertArrayEquals(new Object[] {}, lambda.captured);
        assertArrayEquals(new Type[] {}, lambda.static_.capturedTypes);
        assertArrayEquals(new Type[] {}, lambda.static_.argumentTypes);

        lambda = inspectStringConsumer((s) -> {
        });
        assertEquals(null, lambda.this_);
        assertArrayEquals(new Object[] {}, lambda.captured);
        assertArrayEquals(new Type[] {}, lambda.static_.capturedTypes);
        assertArrayEquals(new Type[] { Object.class }, lambda.static_.argumentTypes);

        lambda = inspectRunnable(() -> System.out.print(str));
        assertEquals(this, lambda.this_);
        assertArrayEquals(new Object[] {}, lambda.captured);
        assertArrayEquals(new Type[] {}, lambda.static_.capturedTypes);
        assertArrayEquals(new Type[] {}, lambda.static_.argumentTypes);

        lambda = inspectStringConsumer((s) -> System.out.print(str));
        assertEquals(this, lambda.this_);
        assertArrayEquals(new Object[] {}, lambda.captured);
        assertArrayEquals(new Type[] {}, lambda.static_.capturedTypes);
        assertArrayEquals(new Type[] { Object.class }, lambda.static_.argumentTypes);

        int i = 4;

        lambda = inspectRunnable(() -> System.out.print(i));
        assertEquals(null, lambda.this_);
        assertArrayEquals(new Object[] { 4 }, lambda.captured);
        assertArrayEquals(new Type[] { Integer.TYPE }, lambda.static_.capturedTypes);
        assertArrayEquals(new Type[] {}, lambda.static_.argumentTypes);

        lambda = inspectStringConsumer((s) -> System.out.print(i));
        assertEquals(null, lambda.this_);
        assertArrayEquals(new Object[] { 4 }, lambda.captured);
        assertArrayEquals(new Type[] { Integer.TYPE }, lambda.static_.capturedTypes);
        assertArrayEquals(new Type[] { Object.class }, lambda.static_.argumentTypes);

        lambda = inspectRunnable(() -> System.out.print(str + i));
        assertEquals(this, lambda.this_);
        assertArrayEquals(new Object[] { 4 }, lambda.captured);
        assertArrayEquals(new Type[] { Integer.TYPE }, lambda.static_.capturedTypes);
        assertArrayEquals(new Type[] {}, lambda.static_.argumentTypes);

        lambda = inspectStringConsumer((s) -> System.out.print(str + i));
        assertEquals(this, lambda.this_);
        assertArrayEquals(new Object[] { 4 }, lambda.captured);
        assertArrayEquals(new Type[] { Integer.TYPE }, lambda.static_.capturedTypes);
        assertArrayEquals(new Type[] { Object.class }, lambda.static_.argumentTypes);
    }

    Lambda inspectStringSupplier(Supplier<String> lambda) {
        return LambdaInspector.inspect(lambda);
    }

    @Test
    public void testNew() {
        Lambda lambda = inspectStringSupplier(() -> new String("foo"));
        assertEquals("new java.lang.String(\"foo\")", lambda.static_.expression.toString());
    }

    String getInvokedMethodName(Runnable lambda) {
        return LambdaInspector.inspect(lambda).static_.accessedMemberInfo.member.getName();
    }

    @Test
    public void readmeSample() {
        assertEquals("length", getInvokedMethodName(() -> "".length()));
    }
}
