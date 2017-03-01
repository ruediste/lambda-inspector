package com.github.ruediste.lambdaInspector.expr;

public class InstanceOfExpression extends ExpressionBase {

    private final Expression expr;
    private final Class<?> queryType;

    public InstanceOfExpression(Expression expr, Class<?> queryType) {
        super(Boolean.TYPE);
        this.expr = expr;
        this.queryType = queryType;
    }

    @Override
    public <T> T accept(ExpressionVisitor<T> visitor) {
        return visitor.visit(this);
    }

    public Expression getExpr() {
        return expr;
    }

    public Class<?> getQueryType() {
        return queryType;
    }

    @Override
    public String toString() {
        return "(" + expr + " instanceof " + queryType.getName() + ")";
    }
}
