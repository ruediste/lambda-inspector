package com.github.ruediste.lambdaInspector.expr;

public interface Expression {

    Class<?> getType();

    <T> T accept(ExpressionVisitor<T> visitor);

    @Override
    boolean equals(Object obj);

    @Override
    int hashCode();
}