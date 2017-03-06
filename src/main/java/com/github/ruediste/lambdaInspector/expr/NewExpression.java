package com.github.ruediste.lambdaInspector.expr;

public class NewExpression extends Expression {

    public NewExpression(Class<?> type) {
        super(type);
    }

    @Override
    public <T> T accept(ExpressionVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public String toString() {
        return "new " + type.getName() + "()";
    }
}
