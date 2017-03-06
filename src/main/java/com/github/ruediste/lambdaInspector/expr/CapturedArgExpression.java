package com.github.ruediste.lambdaInspector.expr;

public class CapturedArgExpression extends Expression {

    private int index;

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
