package com.github.ruediste.lambdaInspector.expr;

public class ReturnAddressExpression extends ExpressionBase {

    public ReturnAddressExpression() {
        super(null);
    }

    @Override
    public <T> T accept(ExpressionVisitor<T> visitor) {
        return visitor.visit(this);
    }

}
