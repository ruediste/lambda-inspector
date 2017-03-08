package com.github.ruediste.lambdaInspector;

import java.util.ArrayList;
import java.util.List;

import com.github.ruediste.lambdaInspector.expr.ArgumentExpression;
import com.github.ruediste.lambdaInspector.expr.ArrayLengthExpression;
import com.github.ruediste.lambdaInspector.expr.ArrayLoadExpression;
import com.github.ruediste.lambdaInspector.expr.BinaryArithmeticExpression;
import com.github.ruediste.lambdaInspector.expr.CapturedArgExpression;
import com.github.ruediste.lambdaInspector.expr.CastExpression;
import com.github.ruediste.lambdaInspector.expr.ConstExpression;
import com.github.ruediste.lambdaInspector.expr.Expression;
import com.github.ruediste.lambdaInspector.expr.ExpressionVisitor;
import com.github.ruediste.lambdaInspector.expr.GetFieldExpression;
import com.github.ruediste.lambdaInspector.expr.InstanceOfExpression;
import com.github.ruediste.lambdaInspector.expr.MethodInvocationExpression;
import com.github.ruediste.lambdaInspector.expr.NewArrayExpression;
import com.github.ruediste.lambdaInspector.expr.NewExpression;
import com.github.ruediste.lambdaInspector.expr.ReturnAddressExpression;
import com.github.ruediste.lambdaInspector.expr.ThisExpression;
import com.github.ruediste.lambdaInspector.expr.UnaryExpression;
import com.github.ruediste.lambdaInspector.expr.UnknownExpression;

public class DeepIdentityExpressionVisitor implements ExpressionVisitor<Expression> {

    @Override
    public Expression visit(MethodInvocationExpression expr) {
        Expression target = null;
        if (expr.target != null)
            target = expr.target.accept(this);

        List<Expression> args = new ArrayList<>();
        boolean argsDiffer = false;
        for (Expression arg : expr.args) {
            Expression tmp = arg.accept(this);
            argsDiffer |= tmp != arg;
            args.add(tmp);
        }

        if (target == expr.target && !argsDiffer)
            return expr;
        else
            return new MethodInvocationExpression(expr.method, target, args);
    }

    @Override
    public Expression visit(ConstExpression constExpression) {
        return constExpression;
    }

    @Override
    public Expression visit(ReturnAddressExpression returnAddressExpression) {
        return returnAddressExpression;
    }

    @Override
    public Expression visit(GetFieldExpression expr) {
        Expression target = null;
        if (expr.target != null)
            target = expr.target.accept(this);
        if (target == expr.target)
            return expr;
        else
            return new GetFieldExpression(target, expr.field);
    }

    @Override
    public Expression visit(NewExpression expr) {
        Expression target = null;
        if (expr.target != null)
            target = expr.target.accept(this);

        List<Expression> args = new ArrayList<>();
        boolean argsDiffer = false;
        for (Expression arg : expr.args) {
            Expression tmp = arg.accept(this);
            argsDiffer |= tmp != arg;
            args.add(tmp);
        }

        if (target == expr.target && !argsDiffer)
            return expr;
        else
            return new NewExpression(expr.constructor, target, args);
    }

    @Override
    public Expression visit(ArgumentExpression argumentExpression) {
        return argumentExpression;
    }

    @Override
    public Expression visit(CapturedArgExpression capturedArgExpression) {
        return capturedArgExpression;
    }

    @Override
    public Expression visit(UnaryExpression expr) {
        Expression arg = expr.argument.accept(this);
        if (arg != expr.argument)
            return new UnaryExpression(expr.type, arg);
        else
            return expr;
    }

    @Override
    public Expression visit(NewArrayExpression expr) {
        Expression length = expr.length.accept(this);
        if (length != expr.length)
            return new NewArrayExpression(expr.type, length, expr.isMultiDimensional);
        else
            return expr;
    }

    @Override
    public Expression visit(ArrayLengthExpression expr) {
        Expression array = expr.array.accept(this);
        if (expr.array == array)
            return expr;
        else
            return new ArrayLengthExpression(array);
    }

    @Override
    public Expression visit(CastExpression expr) {
        Expression arg = expr.expr.accept(this);
        if (arg != expr.expr)
            return new CastExpression(expr.type, arg);
        else
            return expr;
    }

    @Override
    public Expression visit(InstanceOfExpression expr) {
        Expression arg = expr.expr.accept(this);
        if (arg != expr.expr)
            return new InstanceOfExpression(arg, expr.queryType);
        else
            return expr;
    }

    @Override
    public Expression visit(ThisExpression thisExpression) {
        return thisExpression;
    }

    @Override
    public Expression visit(ArrayLoadExpression expr) {
        Expression array = expr.array.accept(this);
        Expression index = expr.index.accept(this);
        if (array != expr.array || index != expr.index)
            return new ArrayLoadExpression(array, index);
        else
            return expr;
    }

    @Override
    public Expression visit(BinaryArithmeticExpression expr) {
        Expression exp1 = expr.exp1.accept(this);
        Expression exp2 = expr.exp2.accept(this);
        if (exp1 != expr.exp1 || exp2 != expr.exp2)
            return new BinaryArithmeticExpression(expr.op, exp1, exp2);
        else
            return expr;
    }

    @Override
    public Expression visit(UnknownExpression unknownExpression) {
        return unknownExpression;
    }

}
