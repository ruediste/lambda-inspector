package com.github.ruediste.lambdaInspector.expr;

import static java.util.stream.Collectors.joining;

import java.lang.reflect.Executable;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.Objects;

public class MethodInvocationExpression extends Expression {

    public MethodInvocationExpression(Executable method, Expression target, List<Expression> args) {
        super(method instanceof Method ? ((Method) method).getReturnType() : method.getDeclaringClass());
        this.method = method;
        this.target = target;
        this.args = args;
    }

    public Executable method;
    public Expression target;
    public List<Expression> args;

    @Override
    public <T> T accept(ExpressionVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public String toString() {
        String invocation = "." + method.getName() + "("
                + args.stream().map(x -> Objects.toString(x)).collect(joining(",")) + ")";
        if (Modifier.isStatic(method.getModifiers())) {
            return method.getDeclaringClass().getName() + invocation;
        } else {
            return target + invocation;
        }
    }
}
