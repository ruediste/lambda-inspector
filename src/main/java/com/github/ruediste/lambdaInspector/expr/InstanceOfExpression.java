package com.github.ruediste.lambdaInspector.expr;

public class InstanceOfExpression extends Expression {

    public final Expression expr;
    public final Class<?> queryType;

    public InstanceOfExpression(Expression expr, Class<?> queryType) {
        super(Boolean.TYPE);
        this.expr = expr;
        this.queryType = queryType;
    }

    @Override
    public <T> T accept(ExpressionVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public String toString() {
        return "(" + expr + " instanceof " + queryType.getName() + ")";
    }
}
