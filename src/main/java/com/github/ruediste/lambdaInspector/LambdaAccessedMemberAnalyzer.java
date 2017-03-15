package com.github.ruediste.lambdaInspector;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

import com.github.ruediste.lambdaInspector.LambdaStatic.LambdaAccessedMemberInfo;
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

public class LambdaAccessedMemberAnalyzer {

    public static LambdaAccessedMemberInfo analyze(LambdaStatic lambda) {
        return analyze(lambda.expression);
    }

    public static LambdaAccessedMemberInfo analyze(Expression expression) {
        if (expression == null)
            return null;

        return expression.accept(new CastRemover()).accept(new AccessedMemberExtractor());
    }

    private static class CastRemover extends IdentityExpressionVisitor {
        static private Set<Method> boxingMethods = new HashSet<>();
        static private Set<Method> unboxingMethods = new HashSet<>();
        static {
            try {
                boxingMethods.add(Long.class.getMethod("valueOf", Long.TYPE));
                boxingMethods.add(Integer.class.getMethod("valueOf", Integer.TYPE));
                boxingMethods.add(Short.class.getMethod("valueOf", Short.TYPE));
                boxingMethods.add(Byte.class.getMethod("valueOf", Byte.TYPE));
                boxingMethods.add(Character.class.getMethod("valueOf", Character.TYPE));
                boxingMethods.add(Double.class.getMethod("valueOf", Double.TYPE));
                boxingMethods.add(Float.class.getMethod("valueOf", Float.TYPE));

                unboxingMethods.add(Long.class.getMethod("longValue"));
                unboxingMethods.add(Integer.class.getMethod("intValue"));
                unboxingMethods.add(Short.class.getMethod("shortValue"));
                unboxingMethods.add(Byte.class.getMethod("byteValue"));
                unboxingMethods.add(Character.class.getMethod("charValue"));
                unboxingMethods.add(Double.class.getMethod("doubleValue"));
                unboxingMethods.add(Float.class.getMethod("floatValue"));
            } catch (NoSuchMethodException | SecurityException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public Expression visit(MethodInvocationExpression expr) {
            if (boxingMethods.contains(expr.method))
                return expr.args.get(0);
            else if (unboxingMethods.contains(expr.method))
                return expr.target;
            else
                return expr;
        }

        @Override
        public Expression visit(CastExpression expr) {
            return expr.expr;
        }
    }

    private static class AccessedMemberExtractor implements ExpressionVisitor<LambdaAccessedMemberInfo> {

        @Override
        public LambdaAccessedMemberInfo visit(MethodInvocationExpression expr) {
            LambdaAccessedMemberInfo info = new LambdaAccessedMemberInfo();
            info.member = expr.method;
            info.expr = expr;
            info.base = expr.target;
            return info;
        }

        @Override
        public LambdaAccessedMemberInfo visit(GetFieldExpression expr) {
            LambdaAccessedMemberInfo info = new LambdaAccessedMemberInfo();
            info.member = expr.field;
            info.base = expr.target;
            info.expr = expr;
            return info;
        }

        @Override
        public LambdaAccessedMemberInfo visit(ConstExpression constExpression) {
            return null;
        }

        @Override
        public LambdaAccessedMemberInfo visit(ReturnAddressExpression returnAddressExpression) {
            return null;
        }

        @Override
        public LambdaAccessedMemberInfo visit(NewExpression newExpression) {
            return null;
        }

        @Override
        public LambdaAccessedMemberInfo visit(ArgumentExpression argumentExpression) {
            return null;
        }

        @Override
        public LambdaAccessedMemberInfo visit(CapturedArgExpression capturedArgExpression) {
            return null;
        }

        @Override
        public LambdaAccessedMemberInfo visit(UnaryExpression unaryExpression) {
            return null;
        }

        @Override
        public LambdaAccessedMemberInfo visit(NewArrayExpression newArrayExpression) {
            return null;
        }

        @Override
        public LambdaAccessedMemberInfo visit(ArrayLengthExpression arrayLengthExpression) {
            return null;
        }

        @Override
        public LambdaAccessedMemberInfo visit(CastExpression castExpression) {
            return null;
        }

        @Override
        public LambdaAccessedMemberInfo visit(InstanceOfExpression instanceOfExpression) {
            return null;
        }

        @Override
        public LambdaAccessedMemberInfo visit(ThisExpression thisExpression) {
            return null;
        }

        @Override
        public LambdaAccessedMemberInfo visit(ArrayLoadExpression arrayLoadExpression) {
            return null;
        }

        @Override
        public LambdaAccessedMemberInfo visit(BinaryArithmeticExpression binaryArithmeticExpression) {
            return null;
        }

        @Override
        public LambdaAccessedMemberInfo visit(UnknownExpression unknownExpression) {
            return null;
        }
    }

}
