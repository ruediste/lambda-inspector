package com.github.ruediste.lambdaInspector.expr;

public interface Expression {

    Class<?> getType();

    <T> T accept(ExpressionVisitor<T> visitor);

}