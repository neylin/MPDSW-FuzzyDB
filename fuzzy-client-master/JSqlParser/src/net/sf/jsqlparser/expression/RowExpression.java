package net.sf.jsqlparser.expression;

import java.util.List;

import net.sf.jsqlparser.expression.operators.relational.ExpressionList;

public class RowExpression implements Expression {

    private ExpressionList expressions;
    private final String expressionType = "row";

    public RowExpression(List<Expression> expressions) {
        this.expressions = new ExpressionList(expressions);
    }

    public ExpressionList getExpressions() {
        return this.expressions;
    }

    public void accept(ExpressionVisitor expressionVisitor) throws Exception {
        expressionVisitor.visit(this);
    }

    @Override
    public String getExpressionType() {
        return expressionType;
    }

}