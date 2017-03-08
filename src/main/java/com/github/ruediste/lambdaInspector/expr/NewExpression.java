package com.github.ruediste.lambdaInspector.expr;

import static java.util.stream.Collectors.joining;

import java.lang.reflect.Constructor;
import java.util.List;
import java.util.Objects;

public class NewExpression extends Expression {
    public Constructor<?> constructor;
    public Expression target;
    public List<Expression> args;

    public NewExpression(Class<?> type) {
        super(type);
    }

    public NewExpression(Constructor<?> constructor, Expression target, List<Expression> args) {
        super(constructor.getDeclaringClass());
        this.constructor = constructor;
        this.target = target;
        this.args = args;
    }

    @Override
    public <T> T accept(ExpressionVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public String toString() {
        return "new " + type.getName() + "(" + args.stream().map(x -> Objects.toString(x)).collect(joining(",")) + ")";
    }
}
