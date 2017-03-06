package com.github.ruediste.lambdaInspector.expr;

public class NewArrayExpression extends Expression {

    public final boolean isMultiDimensional;
    public final Expression length;

    public NewArrayExpression(Class<?> arrayType, Expression length, boolean isMultiDimensional) {
        super(arrayType);
        this.length = length;
        this.isMultiDimensional = isMultiDimensional;
    }

    @Override
    public <T> T accept(ExpressionVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public String toString() {
        return "new " + type.getComponentType().getSimpleName() + "[" + getLength() + "]";
    }

    public Expression getLength() {
        return length;
    }

    public boolean isMultiDimensional() {
        return isMultiDimensional;
    }
}
