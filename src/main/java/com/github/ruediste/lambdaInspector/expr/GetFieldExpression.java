package com.github.ruediste.lambdaInspector.expr;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public class GetFieldExpression extends ExpressionBase {

    private Field field;
    private Expression target;

    public GetFieldExpression(Expression target, Field field) {
        super(field.getType());
        this.target = target;
        this.field = field;
    }

    @Override
    public <T> T accept(ExpressionVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public String toString() {
        if (Modifier.isStatic(field.getModifiers())) {
            return field.getDeclaringClass().getName() + "." + field.getName();
        } else
            return target + "." + field.getName();
    }

}
