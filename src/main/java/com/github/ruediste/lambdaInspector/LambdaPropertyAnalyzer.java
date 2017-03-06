package com.github.ruediste.lambdaInspector;

import com.github.ruediste.lambdaInspector.LambdaStatic.LambdaPropertyInfo;
import com.github.ruediste.lambdaInspector.expr.ArgumentExpression;
import com.github.ruediste.lambdaInspector.expr.ArrayLengthExpression;
import com.github.ruediste.lambdaInspector.expr.ArrayLoadExpression;
import com.github.ruediste.lambdaInspector.expr.BinaryArithmeticExpression;
import com.github.ruediste.lambdaInspector.expr.CapturedArgExpression;
import com.github.ruediste.lambdaInspector.expr.CastExpression;
import com.github.ruediste.lambdaInspector.expr.ConstExpression;
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

public class LambdaPropertyAnalyzer {

    public LambdaPropertyInfo analyze(LambdaStatic lambda) {
        if (lambda.expression == null)
            return null;

        return lambda.expression.accept(new PropertyInfoExtractor());
    }

    private static class PropertyInfoExtractor implements ExpressionVisitor<LambdaPropertyInfo> {

        @Override
        public LambdaPropertyInfo visit(MethodInvocationExpression expr) {
            LambdaPropertyInfo info = new LambdaPropertyInfo();
            info.accessor = expr.method;
            info.base = expr.target;
            return info;
        }

        @Override
        public LambdaPropertyInfo visit(GetFieldExpression expr) {
            LambdaPropertyInfo info = new LambdaPropertyInfo();
            info.accessor = expr.field;
            info.base = expr.target;
            return info;
        }

        @Override
        public LambdaPropertyInfo visit(ConstExpression constExpression) {
            return null;
        }

        @Override
        public LambdaPropertyInfo visit(ReturnAddressExpression returnAddressExpression) {
            return null;
        }

        @Override
        public LambdaPropertyInfo visit(NewExpression newExpression) {
            return null;
        }

        @Override
        public LambdaPropertyInfo visit(ArgumentExpression argumentExpression) {
            return null;
        }

        @Override
        public LambdaPropertyInfo visit(CapturedArgExpression capturedArgExpression) {
            return null;
        }

        @Override
        public LambdaPropertyInfo visit(UnaryExpression unaryExpression) {
            return null;
        }

        @Override
        public LambdaPropertyInfo visit(NewArrayExpression newArrayExpression) {
            return null;
        }

        @Override
        public LambdaPropertyInfo visit(ArrayLengthExpression arrayLengthExpression) {
            return null;
        }

        @Override
        public LambdaPropertyInfo visit(CastExpression castExpression) {
            return null;
        }

        @Override
        public LambdaPropertyInfo visit(InstanceOfExpression instanceOfExpression) {
            return null;
        }

        @Override
        public LambdaPropertyInfo visit(ThisExpression thisExpression) {
            return null;
        }

        @Override
        public LambdaPropertyInfo visit(ArrayLoadExpression arrayLoadExpression) {
            return null;
        }

        @Override
        public LambdaPropertyInfo visit(BinaryArithmeticExpression binaryArithmeticExpression) {
            return null;
        }

        @Override
        public LambdaPropertyInfo visit(UnknownExpression unknownExpression) {
            return null;
        }
    }

}
