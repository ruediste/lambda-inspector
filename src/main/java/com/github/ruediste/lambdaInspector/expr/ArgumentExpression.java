package com.github.ruediste.lambdaInspector.expr;

public class ArgumentExpression extends Expression {

    public final int index;

    public ArgumentExpression(Class<?> type, int index) {
        super(type);
        this.index = index;
    }

    @Override
    public <T> T accept(ExpressionVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public String toString() {
        return "arg$" + index;
    }

}
