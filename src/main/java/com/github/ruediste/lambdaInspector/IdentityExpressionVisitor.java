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

public class IdentityExpressionVisitor implements ExpressionVisitor<Expression> {

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
    public Expression visit(GetFieldExpression getFieldExpression) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Expression visit(NewExpression newExpression) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Expression visit(ArgumentExpression argumentExpression) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Expression visit(CapturedArgExpression capturedArgExpression) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Expression visit(UnaryExpression unaryExpression) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Expression visit(NewArrayExpression newArrayExpression) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Expression visit(ArrayLengthExpression arrayLengthExpression) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Expression visit(CastExpression castExpression) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Expression visit(InstanceOfExpression instanceOfExpression) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Expression visit(ThisExpression thisExpression) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Expression visit(ArrayLoadExpression arrayLoadExpression) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Expression visit(BinaryArithmeticExpression binaryArithmeticExpression) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Expression visit(UnknownExpression unknownExpression) {
        // TODO Auto-generated method stub
        return null;
    }

}
