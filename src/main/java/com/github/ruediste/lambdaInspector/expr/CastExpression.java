package com.github.ruediste.lambdaInspector.expr;

public class CastExpression extends ExpressionBase {

    private final Expression expr;

    public CastExpression(Class<?> targetType, Expression expr) {
        super(targetType);
        this.expr = expr;
    }

    @Override
    public <T> T accept(ExpressionVisitor<T> visitor) {
        return visitor.visit(this);
    }

    public Expression getExpr() {
        return expr;
    }

    @Override
    public String toString() {
        return "((" + type.getName() + ")" + expr + ")";
    }

}
