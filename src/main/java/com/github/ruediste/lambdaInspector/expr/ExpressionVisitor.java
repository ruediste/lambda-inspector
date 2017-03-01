package com.github.ruediste.lambdaInspector.expr;

public interface ExpressionVisitor<T> {
    // T visit(Expression expr);

    T visit(MethodInvocationExpression expr);

    T visit(ConstExpression constExpression);

    T visit(ReturnAddressExpression returnAddressExpression);

    T visit(GetFieldExpression getFieldExpression);

    T visit(NewExpression newExpression);

    T visit(ArgumentExpression argumentExpression);

    T visit(CapturedArgExpression capturedArgExpression);

    T visit(UnaryExpression unaryExpression);

    T visit(NewArrayExpression newArrayExpression);

    T visit(ArrayLengthExpression arrayLengthExpression);

    T visit(CastExpression castExpression);

    T visit(InstanceOfExpression instanceOfExpression);

    T visit(ThisExpression thisExpression);

}
