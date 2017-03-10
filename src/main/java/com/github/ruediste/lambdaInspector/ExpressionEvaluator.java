package com.github.ruediste.lambdaInspector;

import static java.util.stream.Collectors.toList;

import java.lang.reflect.Array;
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

public class ExpressionEvaluator {

    public static Object evaluate(Expression exp, Lambda lambda, Object[] args) {
        return evaluate(exp, lambda.this_, args, lambda.captured);
    }

    public static Object evaluate(Expression exp, Object this_, Object[] args, Object[] captured) {
        return exp.accept(new EvalVisitor(this_, args, captured));
    }

    private static class EvalVisitor implements ExpressionVisitor<Object> {

        private Object this_;
        private Object[] args;
        private Object[] captured;

        public EvalVisitor(Object this_, Object[] args, Object[] captured) {
            this.this_ = this_;
            this.args = args;
            this.captured = captured;
        }

        @Override
        public Object visit(MethodInvocationExpression expr) {

            Object target = null;
            if (expr.target != null)
                target = expr.target.accept(this);
            List<Object> args = expr.args.stream().map(x -> x.accept(this)).collect(toList());
            try {
                return expr.method.invoke(target, args.toArray());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public Object visit(ConstExpression constExpression) {
            return constExpression.value;
        }

        @Override
        public Object visit(ReturnAddressExpression returnAddressExpression) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Object visit(GetFieldExpression expr) {
            Object target = expr.target.accept(this);
            try {
                return expr.field.get(target);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public Object visit(NewExpression expr) {
            List<Object> args = expr.args.stream().map(x -> x.accept(this)).collect(toList());
            try {
                return expr.constructor.newInstance(args.toArray());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public Object visit(ArgumentExpression expr) {
            return args[expr.index];
        }

        @Override
        public Object visit(CapturedArgExpression exp) {
            return captured[exp.index];
        }

        @Override
        public Object visit(UnaryExpression exp) {
            Object arg = exp.argument.accept(this);
            return exp.type.eval(arg);
        }

        @Override
        public Object visit(NewArrayExpression expr) {
            return Array.newInstance(expr.type.getComponentType(), (int) expr.length.accept(this));
        }

        @Override
        public Object visit(ArrayLengthExpression expr) {
            Object array = expr.array.accept(this);
            return Array.getLength(array);
        }

        @Override
        public Object visit(CastExpression castExpression) {
            return castExpression.expr.accept(this);
        }

        @Override
        public Object visit(InstanceOfExpression expr) {
            return expr.queryType.isInstance(expr.expr.accept(this));
        }

        @Override
        public Object visit(ThisExpression thisExpression) {
            return this_;
        }

        @Override
        public Object visit(ArrayLoadExpression expr) {
            Object array = expr.array.accept(this);
            Object index = expr.index.accept(this);
            return Array.get(array, (int) index);
        }

        @Override
        public Object visit(BinaryArithmeticExpression exp) {
            Object arg1 = exp.exp1.accept(this);
            Object arg2 = exp.exp2.accept(this);
            return exp.op.evaluate(arg1, arg2);
        }

        @Override
        public Object visit(UnknownExpression unknownExpression) {
            throw new UnsupportedOperationException();
        }

    }

}
