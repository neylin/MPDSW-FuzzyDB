package fuzzy.type3.translator;

import net.sf.jsqlparser.expression.ExpressionVisitor;
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
	 */
	public UpdateTranslator(ExpressionVisitor expressionVisitor) {
		this.expressionVisitor = expressionVisitor;
	}

	public void translate(Update update) {

	}

	public ExpressionVisitor getExpressionVisitor() {
		return expressionVisitor;
	}

	public void setExpressionVisitor(ExpressionVisitor visitor) {
		expressionVisitor = visitor;
	}

}
