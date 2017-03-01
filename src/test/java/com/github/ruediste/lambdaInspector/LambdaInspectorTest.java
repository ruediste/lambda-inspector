package com.github.ruediste.lambdaInspector;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.lang.reflect.Type;

import org.junit.Test;

public class LambdaInspectorTest {

    @Test
    public void testInspector() throws Exception {
        LambdaInspector.setup();
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

    @Test
    public void testInspect() throws Exception {
        LambdaTestCases testCases = new LambdaTestCases();

        Lambda lambda = LambdaInspector.inspect(testCases.simple());
        assertEquals(null, lambda.this_);
        assertArrayEquals(new Object[] {}, lambda.captured);
        assertArrayEquals(new Type[] {}, lambda.capturedTypes);
        assertArrayEquals(new Type[] {}, lambda.argumentTypes);

        lambda = LambdaInspector.inspect(testCases.simpleArg());
        assertEquals(null, lambda.this_);
        assertArrayEquals(new Object[] {}, lambda.captured);
        assertArrayEquals(new Type[] {}, lambda.capturedTypes);
        assertArrayEquals(new Type[] { Object.class }, lambda.argumentTypes);

        lambda = LambdaInspector.inspect(testCases.instance());
        assertEquals(testCases, lambda.this_);
        assertArrayEquals(new Object[] {}, lambda.captured);
        assertArrayEquals(new Type[] {}, lambda.capturedTypes);
        assertArrayEquals(new Type[] {}, lambda.argumentTypes);

        lambda = LambdaInspector.inspect(testCases.instanceArg());
        assertEquals(testCases, lambda.this_);
        assertArrayEquals(new Object[] {}, lambda.captured);
        assertArrayEquals(new Type[] {}, lambda.capturedTypes);
        assertArrayEquals(new Type[] { Object.class }, lambda.argumentTypes);

        lambda = LambdaInspector.inspect(testCases.capture());
        assertEquals(null, lambda.this_);
        assertArrayEquals(new Object[] { 4 }, lambda.captured);
        assertArrayEquals(new Type[] { Integer.TYPE }, lambda.capturedTypes);
        assertArrayEquals(new Type[] {}, lambda.argumentTypes);

        lambda = LambdaInspector.inspect(testCases.captureArg());
        assertEquals(null, lambda.this_);
        assertArrayEquals(new Object[] { 4 }, lambda.captured);
        assertArrayEquals(new Type[] { Integer.TYPE }, lambda.capturedTypes);
        assertArrayEquals(new Type[] { Object.class }, lambda.argumentTypes);

        lambda = LambdaInspector.inspect(testCases.instanceCapture());
        assertEquals(testCases, lambda.this_);
        assertArrayEquals(new Object[] { 4 }, lambda.captured);
        assertArrayEquals(new Type[] { Integer.TYPE }, lambda.capturedTypes);
        assertArrayEquals(new Type[] {}, lambda.argumentTypes);

        lambda = LambdaInspector.inspect(testCases.instanceCaptureArg());
        assertEquals(testCases, lambda.this_);
        assertArrayEquals(new Object[] { 4 }, lambda.captured);
        assertArrayEquals(new Type[] { Integer.TYPE }, lambda.capturedTypes);
        assertArrayEquals(new Type[] { Object.class }, lambda.argumentTypes);
    }
}
