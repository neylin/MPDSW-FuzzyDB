package fuzzy.type3.translator;


import net.sf.jsqlparser.expression.ExpressionVisitor;
import net.sf.jsqlparser.expression.operators.relational.ExpressionList;
import net.sf.jsqlparser.expression.operators.relational.ItemsListVisitor;
import net.sf.jsqlparser.statement.replace.Replace;
import net.sf.jsqlparser.statement.select.SelectVisitor;
import net.sf.jsqlparser.statement.select.SubSelect;

/**
 * A class to de-parse (that is, tranform from JSqlParser hierarchy into a string)
 * a {@link net.sf.jsqlparser.statement.replace.Replace}
 */
public class ReplaceTranslator implements ItemsListVisitor {

    protected ExpressionVisitor expressionVisitor;
    protected SelectVisitor selectVisitor;

    public ReplaceTranslator() {
    }

    /**
     * @param expressionVisitor a {@link ExpressionVisitor} to de-parse expressions. It has to share the same<br>
     * StringBuffer (buffer parameter) as this object in order to work
     * @param selectVisitor a {@link SelectVisitor} to de-parse {@link net.sf.jsqlparser.statement.select.Select}s.
     * It has to share the same<br>
     * StringBuffer (buffer parameter) as this object in order to work
     * @param buffer the buffer that will be filled with the select
     */
    public ReplaceTranslator(ExpressionVisitor expressionVisitor, SelectVisitor selectVisitor) {
        this.expressionVisitor = expressionVisitor;
        this.selectVisitor = selectVisitor;
    }

    public void translate(Replace replace) {
//        buffer.append("REPLACE " + replace.getTable().getWholeTableName());
//        if (replace.getItemsList() != null) {
//            if (replace.getColumns() != null) {
//                buffer.append(" (");
//                for (int i = 0; i < replace.getColumns().size(); i++) {
//                    Column column = (Column) replace.getColumns().get(i);
//                    buffer.append(column.getWholeColumnName());
//                    if (i < replace.getColumns().size() - 1) {
//                        buffer.append(", ");
//                    }
//                }
//                buffer.append(") ");
//            } else {
//                buffer.append(" ");
//            }
//
//        } else {
//            buffer.append(" SET ");
//            for (int i = 0; i < replace.getColumns().size(); i++) {
//                Column column = (Column) replace.getColumns().get(i);
//                buffer.append(column.getWholeColumnName() + "=");
//
//                Expression expression = (Expression) replace.getExpressions().get(i);
//                expression.accept(expressionVisitor);
//                if (i < replace.getColumns().size() - 1) {
//                    buffer.append(", ");
//                }
//
//            }
//        }

    }

    public void visit(ExpressionList expressionList) throws Exception {
//        buffer.append(" VALUES (");
//        for (Iterator iter = expressionList.getExpressions().iterator(); iter.hasNext();) {
//            Expression expression = (Expression) iter.next();
//            expression.accept(expressionVisitor);
//            if (iter.hasNext()) {
//                buffer.append(", ");
//            }
//        }
//        buffer.append(")");
    }

    public void visit(SubSelect subSelect) throws Exception {
        subSelect.getSelectBody().accept(selectVisitor);
    }

    public ExpressionVisitor getExpressionVisitor() {
        return expressionVisitor;
    }

    public SelectVisitor getSelectVisitor() {
        return selectVisitor;
    }

    public void setExpressionVisitor(ExpressionVisitor visitor) {
        expressionVisitor = visitor;
    }

    public void setSelectVisitor(SelectVisitor visitor) {
        selectVisitor = visitor;
    }
}
