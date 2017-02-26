package com.github.ruediste.lambdaInspector;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class LambdaInspectorTest {

    @Test
    public void testSetup() throws Exception {
        LambdaInspector.setup();
        exec(() -> System.out.println("Hello World"));

        Runnable run = () -> {
        };
        LambdaInformation info = run.getClass().getAnnotation(LambdaInformation.class);
        assertEquals(internalName(LambdaInspectorTest.class), info.implMethodClassName());
        assertEquals("lambda$1", info.implMethodName());
        assertEquals("()V", info.implMethodDesc());
        assertEquals(Runnable.class, info.samClass());
        assertEquals("run", info.samMethodName());
        assertEquals("()V", info.samMethodDesc());
    }

    private String internalName(Class<?> cls) {
        return cls.getName().replace('.', '/');
    }

    private void exec(Runnable run) {
        run.run();
    }
}
