package net.sf.jsqlparser.expression;

import net.sf.jsqlparser.statement.select.SubSelect;

public class AllComparisonExpression implements Expression {

    private SubSelect subSelect;
    private final String expressionType = "allcomparison";

    public AllComparisonExpression(SubSelect subSelect) {
        this.subSelect = subSelect;
    }

    public SubSelect GetSubSelect() {
        return subSelect;
    }

    public void accept(ExpressionVisitor expressionVisitor) throws Exception {
        expressionVisitor.visit(this);
    }

    @Override
    public String getExpressionType() {
        return expressionType;
    }
}
