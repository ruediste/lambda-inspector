package com.github.ruediste.lambdaInspector.expr;

public class ExpressionVisitorBase<T> implements ExpressionVisitor<T> {
    @Override
    public T visit(Expression expr) {
        return null;
    }

    @Override
    public T visit(MethodInvocationExpression expr) {
        return visit((Expression) expr);
    }

}
