package com.github.ruediste.lambdaInspector.expr;

public class ArrayLoadExpression extends ExpressionBase {

    public final Expression array;
    public final Expression index;

    public ArrayLoadExpression(Expression array, Expression index) {
        super(array.getType().getComponentType());
        this.array = array;
        this.index = index;
    }

    @Override
    public <T> T accept(ExpressionVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public String toString() {
        return array + "[" + index + "]";
    }
}
