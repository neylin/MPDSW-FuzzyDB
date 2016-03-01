package fuzzy.type3.translator;

import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.ExpressionVisitor;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.update.Update;

/**
 * A class to de-parse (that is, tranform from JSqlParser hierarchy into a string)
 * an {@link net.sf.jsqlparser.statement.update.Update}
 */
public class UpdateTranslator {
	protected ExpressionVisitor expressionVisitor;
	
	public UpdateTranslator() {
	}
	
	/**
	 * @param expressionVisitor a {@link ExpressionVisitor} to de-parse expressions. It has to share the same<br>
	 * StringBuffer (buffer parameter) as this object in order to work
	 * @param buffer the buffer that will be filled with the select
	 */
	public UpdateTranslator(ExpressionVisitor expressionVisitor) {
		this.expressionVisitor = expressionVisitor;
	}

	public void translate(Update update) {
//		buffer.append("UPDATE " + update.getTable().getWholeTableName() + " SET ");
//		for (int i = 0; i < update.getColumns().size(); i++) {
//			Column column = (Column) update.getColumns().get(i);
//			buffer.append(column.getWholeColumnName() + "=");
//
//			Expression expression = (Expression) update.getExpressions().get(i);
//			expression.accept(expressionVisitor);
//			if (i < update.getColumns().size() - 1) {
//				buffer.append(", ");
//			}
//
//		}
//		
//		if (update.getWhere() != null) {
//			buffer.append(" WHERE ");
//			update.getWhere().accept(expressionVisitor);
//		}

	}

	public ExpressionVisitor getExpressionVisitor() {
		return expressionVisitor;
	}

	public void setExpressionVisitor(ExpressionVisitor visitor) {
		expressionVisitor = visitor;
	}

}
