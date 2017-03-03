package com.github.ruediste.lambdaInspector.expr;

public class UnknownExpression extends ExpressionBase {

    public UnknownExpression(Class<?> type) {
        super(type);
    }

    @Override
    public <T> T accept(ExpressionVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public String toString() {
        return "<UNKNOWN>";
    }
}
