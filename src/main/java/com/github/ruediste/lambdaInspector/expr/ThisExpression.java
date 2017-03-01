package com.github.ruediste.lambdaInspector.expr;

public class ThisExpression extends ExpressionBase {

    public ThisExpression(Class<?> thisType) {
        super(thisType);
    }

    @Override
    public <T> T accept(ExpressionVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public String toString() {
        return "this";
    }
}
