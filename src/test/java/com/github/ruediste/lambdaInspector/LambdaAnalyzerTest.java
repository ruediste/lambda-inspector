package com.github.ruediste.lambdaInspector;

import static org.junit.Assert.assertEquals;

import org.junit.BeforeClass;
import org.junit.Test;

import com.github.ruediste.lambdaInspector.expr.Expression;

public class LambdaAnalyzerTest {

    @BeforeClass
    public static void beforeClass() {
        LambdaInspector.setup();
    }

    @Test
    public void testSimpe() {
        Expression expr = LambdaInspector.inspect((Runnable) () -> {
            System.out.println("Hello World");
        }).expression;
        assertEquals("java.lang.System.out.println(\"Hello World\")", expr.toString());
    }
}
