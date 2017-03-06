package com.github.ruediste.lambdaInspector;

import com.github.ruediste.lambdaInspector.LambdaStatic.LambdaPropertyInfo;

/**
 * Represents information on a lambda expression
 */
public class Lambda {
    public Object this_;
    public Object[] captured;

    public LambdaStatic stat;

    public LambdaPropertyHandle property;

    public class LambdaPropertyHandle {
        public LambdaPropertyInfo info;

        public LambdaPropertyHandle(LambdaPropertyInfo propertyInfo) {
            info = propertyInfo;
        }

        public Object getBase(Object... args) {
            return info.getBase(Lambda.this, args);
        }
    }
}
