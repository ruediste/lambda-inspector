package com.github.ruediste.lambdaInspector.expr;

public class ExpressionVisitorBase<T> implements ExpressionVisitor<T> {
    public T fallback(Expression expr) {
        return null;
    }

    @Override
    public T visit(MethodInvocationExpression expr) {
        expr.target.accept(this);
        expr.args.forEach(a -> a.accept(this));
        return fallback(expr);
    }

    @Override
    public T visit(ConstExpression constExpression) {
        return fallback(constExpression);
    }

    @Override
    public T visit(ReturnAddressExpression returnAddressExpression) {
        return fallback(returnAddressExpression);
    }

    @Override
    public T visit(GetFieldExpression getFieldExpression) {
        getFieldExpression.target.accept(this);
        return fallback(getFieldExpression);
    }

    @Override
    public T visit(NewExpression newExpression) {
        return fallback(newExpression);
    }

    @Override
    public T visit(ArgumentExpression argumentExpression) {
        return fallback(argumentExpression);
    }

    @Override
    public T visit(CapturedArgExpression capturedArgExpression) {
        return fallback(capturedArgExpression);
    }

    @Override
    public T visit(UnaryExpression unaryExpression) {
        unaryExpression.argument.accept(this);
        return fallback(unaryExpression);
    }

    @Override
    public T visit(NewArrayExpression newArrayExpression) {
        newArrayExpression.length.accept(this);
        return fallback(newArrayExpression);
    }

    @Override
    public T visit(ArrayLengthExpression arrayLengthExpression) {
        arrayLengthExpression.array.accept(this);
        return fallback(arrayLengthExpression);
    }

    @Override
    public T visit(CastExpression castExpression) {
        castExpression.expr.accept(this);
        return fallback(castExpression);
    }

    @Override
    public T visit(InstanceOfExpression instanceOfExpression) {
        instanceOfExpression.expr.accept(this);
        return fallback(instanceOfExpression);
    }

    @Override
    public T visit(ThisExpression thisExpression) {
        return fallback(thisExpression);
    }

    @Override
    public T visit(ArrayLoadExpression arrayLoadExpression) {
        arrayLoadExpression.array.accept(this);
        arrayLoadExpression.index.accept(this);
        return fallback(arrayLoadExpression);
    }

    @Override
    public T visit(BinaryArithmeticExpression binaryArithmeticExpression) {
        binaryArithmeticExpression.exp1.accept(this);
        binaryArithmeticExpression.exp2.accept(this);
        return fallback(binaryArithmeticExpression);
    }

    @Override
    public T visit(UnknownExpression unknownExpression) {
        return fallback(unknownExpression);
    }
}
