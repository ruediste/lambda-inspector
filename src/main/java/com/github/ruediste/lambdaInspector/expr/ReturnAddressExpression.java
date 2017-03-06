package com.github.ruediste.lambdaInspector.expr;

public class ReturnAddressExpression extends Expression {

    public ReturnAddressExpression() {
        super(null);
    }

    @Override
    public <T> T accept(ExpressionVisitor<T> visitor) {
        return visitor.visit(this);
    }

}
