package com.github.ruediste.lambdaInspector;

import java.util.function.Consumer;

public class LambdaTestCases {

    String str = "Hello World";

    public Runnable simple() {
        return () -> {
        };
    }

    public Runnable instance() {
        return () -> System.out.print(str);
    }

    public Runnable capture() {
        int i = 4;
        return () -> System.out.print(i);
    }

    public Runnable instanceCapture() {
        int i = 4;
        return () -> System.out.print(str + i);
    }

    public Consumer<String> simpleArg() {
        return (s) -> {
        };
    }

    public Consumer<String> instanceArg() {
        return (s) -> System.out.print(str);
    }

    public Consumer<String> captureArg() {
        int i = 4;
        return (s) -> System.out.print(i);
    }

    public Consumer<String> instanceCaptureArg() {
        int i = 4;
        return (s) -> System.out.print(str + i);
    }
}
