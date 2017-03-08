package com.github.ruediste.lambdaInspector;

import com.github.ruediste.lambdaInspector.LambdaStatic.LambdaAccessedMemberInfo;

/**
 * Represents information on a lambda expression
 */
public class Lambda {
    public Object this_;
    public Object[] captured;

    public LambdaStatic stat;

    public LambdaPropertyHandle property;

    public class LambdaPropertyHandle {
        public LambdaAccessedMemberInfo info;

        public LambdaPropertyHandle(LambdaAccessedMemberInfo propertyInfo) {
            info = propertyInfo;
        }

        public Object getBase(Object... args) {
            return info.getBase(Lambda.this, args);
        }
    }
}
