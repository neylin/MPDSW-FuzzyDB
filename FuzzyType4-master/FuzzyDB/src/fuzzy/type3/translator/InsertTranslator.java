package fuzzy.type3.translator;


import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.ExpressionVisitor;
import net.sf.jsqlparser.expression.operators.relational.ExpressionList;
import net.sf.jsqlparser.expression.operators.relational.ItemsListVisitor;
import net.sf.jsqlparser.statement.insert.Insert;
import net.sf.jsqlparser.statement.select.SelectVisitor;
import net.sf.jsqlparser.statement.select.SubSelect;

/**
 * A class to de-parse (that is, tranform from JSqlParser hierarchy into a string)
 * an {@link net.sf.jsqlparser.statement.insert.Insert}
 */
public class InsertTranslator implements ItemsListVisitor {

    protected ExpressionVisitor expressionVisitor;
    protected SelectVisitor selectVisitor;

    public InsertTranslator() {
    }

    /**
     * @param expressionVisitor a {@link ExpressionVisitor} to de-parse {@link net.sf.jsqlparser.expression.Expression}s. It has to share the same<br>
     * StringBuffer (buffer parameter) as this object in order to work
     * @param selectVisitor a {@link SelectVisitor} to de-parse {@link net.sf.jsqlparser.statement.select.Select}s.
     * It has to share the same<br>
     * StringBuffer (buffer parameter) as this object in order to work
     * @param buffer the buffer that will be filled with the insert
     */
    public InsertTranslator(ExpressionVisitor expressionVisitor, SelectVisitor selectVisitor) {
        this.expressionVisitor = expressionVisitor;
        this.selectVisitor = selectVisitor;
    }

    public void translate(Insert insert) {
//        buffer.append("INSERT INTO ");
//        buffer.append(insert.getTable().getWholeTableName());
//        if (insert.getColumns() != null) {
//            buffer.append("(");
//            for (Iterator iter = insert.getColumns().iterator(); iter.hasNext();) {
//                Column column = (Column) iter.next();
//                buffer.append(column.getColumnName());
//                if (iter.hasNext()) {
//                    buffer.append(", ");
//                }
//            }
//            buffer.append(")");
//        }
//
//        insert.getItemsList().accept(this);

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
