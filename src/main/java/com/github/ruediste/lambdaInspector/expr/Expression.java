package com.github.ruediste.lambdaInspector.expr;

/**
 * Base class for expressions
 */
public abstract class Expression {

    public Class<?> type;

    public Expression(Class<?> type) {
        this.type = type;
    }

    public abstract <T> T accept(ExpressionVisitor<T> visitor);
}
