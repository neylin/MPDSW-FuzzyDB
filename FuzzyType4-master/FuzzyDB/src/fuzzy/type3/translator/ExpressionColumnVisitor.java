package fuzzy.type3.translator;

import java.util.Iterator;
import java.util.List;

import net.sf.jsqlparser.expression.AllComparisonExpression;
import net.sf.jsqlparser.expression.AnyComparisonExpression;
import net.sf.jsqlparser.expression.ArrayExpression;
import net.sf.jsqlparser.expression.BinaryExpression;
import net.sf.jsqlparser.expression.CastAsExpression;
import net.sf.jsqlparser.expression.CaseExpression;
import net.sf.jsqlparser.expression.DateValue;
import net.sf.jsqlparser.expression.DoubleValue;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.ExpressionVisitor;
import net.sf.jsqlparser.expression.Function;
import net.sf.jsqlparser.expression.fuzzy.FuzzyByExtension;
import net.sf.jsqlparser.expression.fuzzy.FuzzyTrapezoid;
import net.sf.jsqlparser.expression.InverseExpression;
import net.sf.jsqlparser.expression.JdbcParameter;
import net.sf.jsqlparser.expression.LongValue;
import net.sf.jsqlparser.expression.NullValue;
import net.sf.jsqlparser.expression.Parenthesis;
import net.sf.jsqlparser.expression.Relation;
import net.sf.jsqlparser.expression.RowExpression;
import net.sf.jsqlparser.expression.SimilarColumn;
import net.sf.jsqlparser.expression.Similarity;
import net.sf.jsqlparser.expression.StringValue;
import net.sf.jsqlparser.expression.TimeValue;
import net.sf.jsqlparser.expression.TimestampValue;
import net.sf.jsqlparser.expression.WhenClause;
import net.sf.jsqlparser.expression.operators.arithmetic.Addition;
import net.sf.jsqlparser.expression.operators.arithmetic.BitwiseAnd;
import net.sf.jsqlparser.expression.operators.arithmetic.BitwiseOr;
import net.sf.jsqlparser.expression.operators.arithmetic.BitwiseXor;
import net.sf.jsqlparser.expression.operators.arithmetic.Concat;
import net.sf.jsqlparser.expression.operators.arithmetic.Division;
import net.sf.jsqlparser.expression.operators.arithmetic.Multiplication;
import net.sf.jsqlparser.expression.operators.arithmetic.Subtraction;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.operators.conditional.OrExpression;
import net.sf.jsqlparser.expression.operators.relational.Between;
import net.sf.jsqlparser.expression.operators.relational.EqualsTo;
import net.sf.jsqlparser.expression.operators.relational.ExpressionList;
import net.sf.jsqlparser.expression.operators.relational.GreaterThan;
import net.sf.jsqlparser.expression.operators.relational.GreaterThanEquals;
import net.sf.jsqlparser.expression.operators.relational.InExpression;
import net.sf.jsqlparser.expression.operators.relational.IsNullExpression;
import net.sf.jsqlparser.expression.operators.relational.ItemsListVisitor;
import net.sf.jsqlparser.expression.operators.relational.LikeExpression;
import net.sf.jsqlparser.expression.operators.relational.ExistsExpression;
import net.sf.jsqlparser.expression.operators.relational.Matches;
import net.sf.jsqlparser.expression.operators.relational.MinorThan;
import net.sf.jsqlparser.expression.operators.relational.MinorThanEquals;
import net.sf.jsqlparser.expression.operators.relational.NotEqualsTo;
import net.sf.jsqlparser.statement.select.AllColumns;
import net.sf.jsqlparser.statement.select.AllTableColumns;
import net.sf.jsqlparser.statement.select.SelectExpressionItem;
import net.sf.jsqlparser.statement.select.SelectItemVisitor;
import net.sf.jsqlparser.statement.select.SubSelect;

/**
 * An abstract class focused on finding the Columns. It's useful when extended
 * and implemented only the visit(Column column) method, where operations can
 * be done.
 */
public abstract class ExpressionColumnVisitor implements ExpressionVisitor, ItemsListVisitor, SelectItemVisitor {

    @Override
    public void visit(Addition addition) throws Exception {
        visitBinaryExpression(addition);
    }

    @Override
    public void visit(AndExpression andExpression) throws Exception {
        visitBinaryExpression(andExpression);
    }

    @Override
    public void visit(Between between) throws Exception{
        between.getLeftExpression().accept(this);
        between.getBetweenExpressionStart().accept(this);
        between.getBetweenExpressionEnd().accept(this);
    }

    @Override
    public void visit(Division division) throws Exception {
        visitBinaryExpression(division);
    }

    @Override
    public void visit(DoubleValue doubleValue) throws Exception {

    }

    @Override
    public void visit(EqualsTo equalsTo) throws Exception {
        visitBinaryExpression(equalsTo);
    }

    @Override
    public void visit(GreaterThan greaterThan) throws Exception {
        visitBinaryExpression(greaterThan);
    }

    @Override
    public void visit(GreaterThanEquals greaterThanEquals) throws Exception {
        visitBinaryExpression(greaterThanEquals);
    }

    @Override
    public void visit(InExpression inExpression) throws Exception {
        inExpression.getLeftExpression().accept(this);
        inExpression.getItemsList().accept(this);
    }

    @Override
    public void visit(InverseExpression inverseExpression) throws Exception {
        inverseExpression.getExpression().accept(this);
    }

    @Override
    public void visit(IsNullExpression isNullExpression) throws Exception {
        isNullExpression.getLeftExpression().accept(this);
    }

    @Override
    public void visit(JdbcParameter jdbcParameter) throws Exception {

    }

    @Override
    public void visit(LikeExpression likeExpression) throws Exception {
        visitBinaryExpression(likeExpression);
    }

    @Override
    public void visit(ExistsExpression existsExpression) throws Exception {
        existsExpression.getRightExpression().accept(this);
    }

    @Override
    public void visit(LongValue longValue) throws Exception {

    }

    @Override
    public void visit(MinorThan minorThan) throws Exception {
        visitBinaryExpression(minorThan);

    }

    @Override
    public void visit(MinorThanEquals minorThanEquals) throws Exception {
        visitBinaryExpression(minorThanEquals);
    }

    @Override
    public void visit(Multiplication multiplication) throws Exception {
        visitBinaryExpression(multiplication);
    }

    @Override
    public void visit(NotEqualsTo notEqualsTo) throws Exception {
        visitBinaryExpression(notEqualsTo);
    }

    @Override
    public void visit(NullValue nullValue) throws Exception {

    }

    @Override
    public void visit(OrExpression orExpression) throws Exception {
        visitBinaryExpression(orExpression);
    }

    @Override
    public void visit(Parenthesis parenthesis) throws Exception {
        parenthesis.getExpression().accept(this);
    }

    @Override
    public void visit(StringValue stringValue) throws Exception {

    }

    @Override
    public void visit(Subtraction subtraction) throws Exception {
        visitBinaryExpression(subtraction);
    }

    private void visitBinaryExpression(BinaryExpression binaryExpression) throws Exception {
        binaryExpression.getLeftExpression().accept(this);
        binaryExpression.getRightExpression().accept(this);
    }

    @Override
    public void visit(Function function) throws Exception {
        if (!function.isAllColumns() && null != function.getParameters()) {
            visit(function.getParameters());
        }
    }

    @Override
    public void visit(ExpressionList expressionList) throws Exception {
        for (Iterator iter = expressionList.getExpressions().iterator(); iter.hasNext();) {
            ((Expression)iter.next()).accept(this);
        }
    }

    @Override
    public void visit(DateValue dateValue) throws Exception {

    }

    @Override
    public void visit(TimestampValue timestampValue) throws Exception {

    }

    @Override
    public void visit(TimeValue timeValue) throws Exception {

    }

    @Override
    public void visit(SubSelect subSelect) throws Exception{

    }

    @Override
    public void visit(CaseExpression caseExpression) throws Exception {
		Expression switchExp = caseExpression.getSwitchExpression();
		if( switchExp != null ) {
			switchExp.accept(this);
		}
		
		List clauses = caseExpression.getWhenClauses();
		for (Iterator iter = clauses.iterator(); iter.hasNext();) {
			Expression exp = (Expression) iter.next();
			exp.accept(this);
		}
		
		Expression elseExp = caseExpression.getElseExpression();
		if( elseExp != null ) {
			elseExp.accept(this);
		}
    }

    @Override
    public void visit(WhenClause whenClause) throws Exception {
        whenClause.getWhenExpression().accept(this);
        whenClause.getThenExpression().accept(this);
    }

    @Override
    public void visit(AllComparisonExpression allComparisonExpression) throws Exception {
        allComparisonExpression.GetSubSelect().accept((ExpressionVisitor)this);
    }

    @Override
    public void visit(AnyComparisonExpression anyComparisonExpression) throws Exception {
        anyComparisonExpression.GetSubSelect().accept((ExpressionVisitor)this);
    }

    @Override
    public void visit(Concat concat) throws Exception {
        visitBinaryExpression(concat);
    }

    @Override
    public void visit(Matches matches) throws Exception {
        visitBinaryExpression(matches);
    }

    @Override
    public void visit(BitwiseAnd bitwiseAnd) throws Exception {
        visitBinaryExpression(bitwiseAnd);
    }

    @Override
    public void visit(BitwiseOr bitwiseOr) throws Exception {
        visitBinaryExpression(bitwiseOr);
    }

    @Override
    public void visit(BitwiseXor bitwiseXor) throws Exception {
        visitBinaryExpression(bitwiseXor);
    }

    @Override
    public void visit(AllColumns ac) throws Exception {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void visit(AllTableColumns atc) throws Exception {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void visit(SelectExpressionItem sei) throws Exception {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void visit(SimilarColumn similarColumn) throws Exception {
        similarColumn.getColumn().accept(this);
    }

    @Override
    public void visit(Similarity similarity) throws Exception {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void visit(Relation relation) throws Exception {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void visit(ArrayExpression array) throws Exception {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void visit(RowExpression row) throws Exception {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void visit(FuzzyByExtension fuzzy) throws Exception {
    }

    @Override
    public void visit(FuzzyTrapezoid fuzzy) throws Exception {
    }

    @Override
    public void visit(CastAsExpression castExpression) throws Exception {
        castExpression.getExpression().accept(this);
    }
}