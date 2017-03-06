package com.github.ruediste.lambdaInspector.expr;

public class ArrayLengthExpression extends Expression {

    public final Expression array;

    public ArrayLengthExpression(Expression array) {
        super(Integer.TYPE);
        this.array = array;
    }

    @Override
    public <T> T accept(ExpressionVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public String toString() {
        return array + ".length";
    }

}
