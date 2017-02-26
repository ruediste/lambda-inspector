package com.github.ruediste.lambdaInspector;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class LambdaInspectorTest {

    @Test
    public void testInspector() throws Exception {
        LambdaInspector.setup();
        Runnable run = () -> {
        };
        LambdaInformation info = run.getClass().getAnnotation(LambdaInformation.class);
        assertEquals(LambdaInspectorTest.class, info.implMethodClass());
        assertEquals("lambda$1", info.implMethodName());
        assertEquals("()V", info.implMethodDesc());
        assertEquals(Runnable.class, info.samClass());
        assertEquals("run", info.samMethodName());
        assertEquals("()V", info.samMethodDesc());
    }
}
