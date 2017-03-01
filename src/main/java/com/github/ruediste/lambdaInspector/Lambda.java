package com.github.ruediste.lambdaInspector;

import java.lang.reflect.Method;

import com.github.ruediste.lambdaInspector.expr.Expression;

/**
 * Represents information on a lambda expression
 */
public class Lambda {
    public Object this_;
    public Object[] captured;
    public Method implementationMethod;
    public Class<?>[] capturedTypes;
    public Class<?>[] argumentTypes;
    public Expression expression;

}
