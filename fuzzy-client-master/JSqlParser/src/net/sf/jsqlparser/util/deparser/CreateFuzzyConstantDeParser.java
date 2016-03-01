package net.sf.jsqlparser.util.deparser;

import java.util.Iterator;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.ExpressionVisitor;
import net.sf.jsqlparser.expression.operators.relational.ExpressionList;
import net.sf.jsqlparser.expression.operators.relational.ItemsListVisitor;
import net.sf.jsqlparser.statement.fuzzy.constant.CreateFuzzyConstant;
import net.sf.jsqlparser.statement.select.SelectVisitor;
import net.sf.jsqlparser.statement.select.SubSelect;

/**
 * A class to de-parse (that is, transform from JSqlParser hierarchy into a
 * string) a {@link net.sf.jsqlparser.statement.create.table.CreateTable}
 */
public class CreateFuzzyConstantDeParser implements ItemsListVisitor {

    protected StringBuffer buffer;
    protected ExpressionVisitor expressionVisitor;
    protected SelectVisitor selectVisitor;

    public CreateFuzzyConstantDeParser(ExpressionVisitor expressionVisitor, SelectVisitor selectVisitor, StringBuffer buffer) {
        this.buffer = buffer;
        this.expressionVisitor = expressionVisitor;
        this.selectVisitor = selectVisitor;
    }

    public void deParse(CreateFuzzyConstant createFuzzyConstant) {
        buffer.append("INSERT INTO information_schema_fuzzy.constants2 ")
                .append("(constant_schema, domain_name, constant_name, value, fuzzy_type)")
                .append(" VALUES (")
                .append("'NULL', '")
                .append(createFuzzyConstant.getDomain())
                .append("','")
                .append(createFuzzyConstant.getName())
                .append("',");
        try {
            createFuzzyConstant.getItemsList().accept(this);
        } catch (Exception e) {
        }
    }

    public void visit(ExpressionList expressionList) {
        for (Iterator iter = expressionList.getExpressions().iterator(); iter.hasNext();) {
            Expression expression = (Expression) iter.next();
            try {
                expression.accept(expressionVisitor);
            } catch (Exception e) {
            }
            buffer.append(", '")
                    .append("NULL")
                    .append("'");
        }
        buffer.append(")");
    }

    public void visit(SubSelect subSelect) {
        try {
            subSelect.getSelectBody().accept(selectVisitor);
        } catch (Exception e) {
        }
    }

    public StringBuffer getBuffer() {
        return buffer;
    }

    public void setBuffer(StringBuffer buffer) {
        this.buffer = buffer;
    }

    public ExpressionVisitor getExpressionVisitor() {
        return expressionVisitor;
    }

    public void setExpressionVisitor(ExpressionVisitor visitor) {
        expressionVisitor = visitor;
    }
}
