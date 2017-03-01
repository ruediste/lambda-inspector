package com.github.ruediste.lambdaInspector;

import java.lang.reflect.Method;
import java.lang.reflect.Type;

import com.github.ruediste.lambdaInspector.expr.Expression;

/**
 * Represents information on a lambda expression
 */
public class Lambda {
    public Object this_;
    public Object[] captured;
    public Method implementationMethod;
    public Type[] capturedTypes;
    public Type[] argumentTypes;
    public Expression expression;

}
