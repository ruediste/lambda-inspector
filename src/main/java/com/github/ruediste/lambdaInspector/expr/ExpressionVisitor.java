package com.github.ruediste.lambdaInspector.expr;

public interface ExpressionVisitor<T> {
    // T visit(Expression expr);

    T visit(MethodInvocationExpression expr);

    T visit(ConstExpression constExpression);

    T visit(ReturnAddressExpression returnAddressExpression);

    T visit(GetFieldExpression getFieldExpression);

    T visit(NewExpression newExpression);

}
