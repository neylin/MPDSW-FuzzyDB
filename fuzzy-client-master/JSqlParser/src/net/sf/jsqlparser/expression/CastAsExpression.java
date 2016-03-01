package net.sf.jsqlparser.expression;

import net.sf.jsqlparser.statement.table.ColDataType;

/**
 * CAST expression AS type
 */
public class CastAsExpression implements Expression {

    private Expression expression;
    private ColDataType type;
    private final String expressionType = "castas";

    public CastAsExpression(Expression expression, ColDataType type) {
        this.expression = expression;
        this.type = type;
    }

    public Expression getExpression() {
        return expression;
    }

    public void setExpression(Expression expression) {
        this.expression = expression;
    }

    public ColDataType getType() {
        return this.type;
    }

    public void setType(ColDataType type) {
        this.type = type;
    }

    public void accept(ExpressionVisitor expressionVisitor) throws Exception {
        expressionVisitor.visit(this);
    }

    public String toString() {
        return "CAST " + expression + " AS " + type;
    }

    @Override
    public String getExpressionType() {
        return expressionType;
    }
}
