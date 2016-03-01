package fuzzy.type3.translator;

import net.sf.jsqlparser.expression.ExpressionVisitor;
import net.sf.jsqlparser.statement.delete.Delete;
import net.sf.jsqlparser.statement.select.SelectVisitor;

/**
 * A class to de-parse (that is, tranform from JSqlParser hierarchy into a string)
 * a {@link net.sf.jsqlparser.statement.delete.Delete}
 */
public class DeleteTranslator {
	protected ExpressionVisitor expressionVisitor;

	public DeleteTranslator() {
	}

	/**
	 * @param expressionVisitor a {@link ExpressionVisitor} to de-parse expressions. It has to share the same<br>
	 * StringBuffer (buffer parameter) as this object in order to work
	 * @param buffer the buffer that will be filled with the select
	 */
	public DeleteTranslator(ExpressionVisitor expressionVisitor) {
		this.expressionVisitor = expressionVisitor;
	}

	public void translate(Delete delete) {
//		buffer.append("DELETE FROM " + delete.getTable().getWholeTableName());
//		if (delete.getWhere() != null) {
//			buffer.append(" WHERE ");
//			delete.getWhere().accept(expressionVisitor);
//		}

	}
	public ExpressionVisitor getExpressionVisitor() {
		return expressionVisitor;
	}

	public void setExpressionVisitor(ExpressionVisitor visitor) {
		expressionVisitor = visitor;
	}

}
