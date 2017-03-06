package com.github.ruediste.lambdaInspector.expr;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Base class for expressions
 */
public abstract class Expression {

    public Class<?> type;

    public Expression(Class<?> type) {
        this.type = type;
    }

    public Class<?> getType() {
        return type;
    }

    public abstract <T> T accept(ExpressionVisitor<T> visitor);

    @Override
    public boolean equals(Object obj) {
        if (obj == this)
            return true;
        if (obj == null)
            return false;
        if (obj.getClass() != getClass())
            return false;
        boolean equal = true;
        for (Field field : allFields()) {
            try {
                field.setAccessible(true);
                equal &= Objects.equals(field.get(this), field.get(obj));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return equal;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        for (Field field : allFields()) {
            try {
                field.setAccessible(true);
                hash = hash * 31 + Objects.hashCode(field.get(this));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return hash;
    }

    private List<Field> allFields() {
        ArrayList<Field> fields = new ArrayList<>();
        Class<?> cls = getClass();
        while (Expression.class.isAssignableFrom(cls)) {
            for (Field field : cls.getDeclaredFields()) {
                fields.add(field);
            }
            cls = cls.getSuperclass();
        }
        return fields;
    }
}
