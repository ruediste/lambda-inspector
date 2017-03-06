package com.github.ruediste.lambdaInspector.expr;

import java.util.Objects;

public class ConstExpression extends Expression {

    public Object value;

    public ConstExpression(Class<?> type, Object value) {
        super(type);
        this.value = value;
    }

    @Override
    public <T> T accept(ExpressionVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public String toString() {
        if (value instanceof String)
            return "\"" + value + "\"";
        else
            return Objects.toString(value);
    }

}
