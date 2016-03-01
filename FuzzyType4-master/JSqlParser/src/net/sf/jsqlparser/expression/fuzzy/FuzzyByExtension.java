package net.sf.jsqlparser.expression.fuzzy;

import java.util.ArrayList;
import java.util.List;

import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.ExpressionVisitor;

public class FuzzyByExtension implements Expression {

    private final String expressionType = "fuzzyextension";

    @Override
    public String getExpressionType() {
        return expressionType;
    }

    public class Element {

        public Double possibility;
        public Expression expression;

        public Element(Double possibility, Expression expression) {
            this.possibility = possibility;
            this.expression = expression;
        }

        public Double getPossibility() {
            return this.possibility;
        }

        public Expression getExpression() {
            return this.expression;
        }
    }

    private ArrayList<Element> elements;

    public FuzzyByExtension(Double possibility, Expression expression) {
        this.elements = new ArrayList();
        this.elements.add(new Element(possibility, expression));
    }

    public void addPossibility(Double possibility, Expression expression) {
        this.elements.add(new Element(possibility, expression));
    }

    public List<Element> getPossibilities() {
        return this.elements;
    }

    public void accept(ExpressionVisitor expressionVisitor) throws Exception {
        expressionVisitor.visit(this);
    }
}
