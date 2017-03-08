package com.github.ruediste.lambdaInspector;

import java.lang.reflect.Member;
import java.lang.reflect.Method;

import com.github.ruediste.lambdaInspector.expr.Expression;

public class LambdaStatic {
    public static class LambdaAccessedMemberInfo {
        public Expression base;

        public Member accessor;

        public Object getBase(Lambda lambda, Object[] args) {
            return ExpressionEvaluator.evaluate(base, lambda, args);
        }
    }

    public Method implementationMethod;
    public Class<?>[] capturedTypes;
    public Class<?>[] argumentTypes;

    public Expression expression;
    public LambdaAccessedMemberInfo accessedMemberInfo;

}
