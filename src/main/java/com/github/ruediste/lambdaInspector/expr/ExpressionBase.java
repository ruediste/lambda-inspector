package com.github.ruediste.lambdaInspector.expr;

/**
 * Base class for expressions
 */
public abstract class ExpressionBase implements Expression {

    public Class<?> type;

    public ExpressionBase(Class<?> type) {
        this.type = type;
    }

    /* (non-Javadoc)
     * @see com.github.ruediste.lambdaInspector.expr.IExpression#getType()
     */
    @Override
    public Class<?> getType() {
        return type;
    }

    /* (non-Javadoc)
     * @see com.github.ruediste.lambdaInspector.expr.IExpression#accept(com.github.ruediste.lambdaInspector.expr.ExpressionVisitor)
     */
    @Override
    public abstract <T> T accept(ExpressionVisitor<T> visitor);
}
