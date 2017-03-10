package com.github.ruediste.lambdaInspector.expr;

public class CapturedArgExpression extends Expression {

    public final int index;

    public CapturedArgExpression(Class<?> type, int index) {
        super(type);
        this.index = index;
    }

    @Override
    public String toString() {
        return "cap$" + index;
    }

    @Override
    public <T> T accept(ExpressionVisitor<T> visitor) {
        return visitor.visit(this);
    }

}
