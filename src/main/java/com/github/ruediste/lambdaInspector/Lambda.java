package com.github.ruediste.lambdaInspector;

import com.github.ruediste.lambdaInspector.LambdaStatic.LambdaAccessedMemberInfo;

/**
 * Represents information on a lambda expression
 */
public class Lambda {
    public Object this_;
    public Object[] captured;

    public LambdaStatic static_;

    public LambdaAccessedMemberHandle memberHandle;

    public class LambdaAccessedMemberHandle {

        public Object getBase(Object... args) {
            return static_.accessedMemberInfo.getBase(Lambda.this, args);
        }

        public Lambda getLambda() {
            return Lambda.this;
        }

        public LambdaAccessedMemberInfo getInfo() {
            return static_.accessedMemberInfo;
        }
    }
}
