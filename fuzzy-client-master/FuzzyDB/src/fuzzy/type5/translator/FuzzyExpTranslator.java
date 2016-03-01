/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fuzzy.type5.translator;

import fuzzy.database.Connector;
import fuzzy.common.translator.FuzzyColumnSet;
import net.sf.jsqlparser.schema.Column;

import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;

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
import net.sf.jsqlparser.statement.select.SelectBody;
import net.sf.jsqlparser.statement.select.SelectExpressionItem;
import net.sf.jsqlparser.statement.select.SelectItemVisitor;
import net.sf.jsqlparser.statement.select.SubSelect;
import fuzzy.helpers.Error;
/**
 * 
 */
public class FuzzyExpTranslator implements ExpressionVisitor, ItemsListVisitor, SelectItemVisitor {

    protected Connector connector;
    protected Expression replacement = null;
    protected String alias = null;
    protected FuzzyColumnSet fuzzyColumnSet;
    private boolean mainselect;

    public FuzzyExpTranslator(Connector connector) {
        this.connector = connector;
        this.mainselect = false;
        this.fuzzyColumnSet = null;
    }

    public FuzzyExpTranslator(Connector connector, boolean mainselect, FuzzyColumnSet fuzzyColumnSet) {
        this.connector = connector;
        this.mainselect = mainselect;
        this.fuzzyColumnSet = fuzzyColumnSet;
    }

    public Expression getReplacement() {
        return this.replacement;
    }

    public void setReplacement(Expression exp) {
        this.replacement = exp;
    }

    public void setMainselect(boolean mainselect) {
        this.mainselect = mainselect;
    }

    // Cosas que vienen de SelectItemVisitor
    @Override
    public void visit(AllColumns ac) throws Exception {
        throw new UnsupportedOperationException(Error.getError("notImplemented"));
    }

    @Override
    public void visit(AllTableColumns atc) throws Exception {
        throw new UnsupportedOperationException(Error.getError("notImplemented"));
    }

    @Override
    public void visit(SelectExpressionItem sei) throws Exception {
        this.setReplacement(null);
        sei.getExpression().accept(this);
        Expression replacement = this.getReplacement();
        if (null != replacement) {
            sei.setExpression(replacement);            
        }
        this.replacement = null;

        if (null != this.alias) {
            sei.setAlias(this.alias);
            this.alias = null;
        }
    }

    // Cosas que vienen de ItemsListVisitor
    @Override
    public void visit(SubSelect subSelect) throws Exception {
        SelectTranslator translator = new SelectTranslator(connector, false);
        SelectBody selectBody = subSelect.getSelectBody();
        selectBody.accept(translator);
    }

    @Override
    public void visit(ExpressionList expressionList) throws Exception {
        List<Expression> expressions = new ArrayList<Expression>();
        for (Iterator iter = expressionList.getExpressions().iterator(); iter.hasNext();) {
            Expression exp = (Expression) iter.next();
            this.setReplacement(null);
            exp.accept(this);
            expressions.add(null == this.getReplacement() ? exp : this.getReplacement());
        }
        expressionList.setExpressions(expressions);
        this.replacement = null;
    }

    // Cosas que vienen de ExpressionVisitor
    @Override
    public void visit(Column column) throws Exception {
        /*
        * Si la columna es difusa tipo 2, y es parte de las columnas que
        * van a aparecer directo en el resultado, se envuelve en un Function
        * para generar su representación en String.
        */ 
        if (!this.mainselect) {
            this.replacement = null;
            return;
        }

        if (null != this.fuzzyColumnSet && null != this.fuzzyColumnSet.get(column)) {
            Function f = new Function();
            f.setName("information_schema_fuzzy.fuzzy5_tostring");
            List<Expression> args = new ArrayList<Expression>();
            args.add(column);
            f.setParameters(new ExpressionList(args));
            this.replacement = f;
            this.alias = column.getColumnName() + "_human_readable";
        }
    }


   @Override
    public void visit(Addition addition) throws Exception {
        visitBinaryExpression(addition);
    }

    @Override
    public void visit(AndExpression andExpression) throws Exception {
        visitBinaryExpression(andExpression);
    }

    @Override
    public void visit(Between between) throws Exception {
        this.replacement = null;
        between.getBetweenExpressionStart().accept(this);
        if (null != this.replacement) {
            between.setBetweenExpressionStart(this.replacement);
        }
        
        this.replacement = null;
        between.getBetweenExpressionEnd().accept(this);
        if (null != this.replacement) {
            between.setBetweenExpressionEnd(this.replacement);
        }
    }

    @Override
    public void visit(Division division) throws Exception {
        visitBinaryExpression(division);
    }

    @Override
    public void visit(DoubleValue doubleValue) throws Exception {
        this.replacement = null;
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
        this.replacement = null;
        inExpression.getLeftExpression().accept(this);
        if (null != this.replacement) {
            inExpression.setLeftExpression(this.replacement);
        }
        inExpression.getItemsList().accept(this);
    }

    @Override
    public void visit(InverseExpression inverseExpression) throws Exception {
        this.replacement = null;
    }

    @Override
    public void visit(IsNullExpression isNullExpression) throws Exception {
        this.replacement = null;
        isNullExpression.getLeftExpression().accept(this);
        if (null != this.replacement) {
            isNullExpression.setLeftExpression(this.replacement);
        }
    }

    @Override
    public void visit(JdbcParameter jdbcParameter) throws Exception {
        this.replacement = null;
    }

    @Override
    public void visit(LikeExpression likeExpression) throws Exception {
        visitBinaryExpression(likeExpression);
    }

    @Override
    public void visit(ExistsExpression existsExpression) throws Exception {
        this.replacement = null;
        existsExpression.getRightExpression().accept(this);
        if (null != this.replacement) {
            existsExpression.setRightExpression(this.replacement);
        }
    }

    @Override
    public void visit(LongValue longValue) throws Exception {
        this.replacement = null;
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
        this.replacement = null;
    }

    @Override
    public void visit(OrExpression orExpression) throws Exception {
        visitBinaryExpression(orExpression);
    }

    @Override
    public void visit(Parenthesis parenthesis) throws Exception {
        this.replacement = null;
        parenthesis.getExpression().accept(this);
        if (null != this.replacement) {
            parenthesis.setExpression(this.replacement);
        }
        this.replacement = null;
    }

    @Override
    public void visit(StringValue stringValue) throws Exception {
        this.replacement = null;
    }

    @Override
    public void visit(Subtraction subtraction) throws Exception {
        visitBinaryExpression(subtraction);
    }

    private void visitBinaryExpression(BinaryExpression binaryExpression) throws Exception {
        this.replacement = null;
        binaryExpression.getLeftExpression().accept(this);
        if (null != this.replacement) {
            binaryExpression.setLeftExpression(this.replacement);
        }

        this.replacement = null;
        binaryExpression.getRightExpression().accept(this);
        if (null != this.replacement) {
            binaryExpression.setRightExpression(this.replacement);
        }

        this.replacement = null;
    }

    @Override
    public void visit(Function function) throws Exception {
        if (!function.isAllColumns() && null != function.getParameters()) {
            visit(function.getParameters());
        }
        this.replacement = null;
    }

    @Override
    public void visit(DateValue dateValue) throws Exception {
        this.replacement = null;
    }

    @Override
    public void visit(TimestampValue timestampValue) throws Exception {
        this.replacement = null;
    }

    @Override
    public void visit(TimeValue timeValue) throws Exception {
        this.replacement = null;
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
        this.replacement = null;
        whenClause.getWhenExpression().accept(this);
        if (null != this.replacement) {
            whenClause.setWhenExpression(this.replacement);
        }

        this.replacement = null;
        whenClause.getThenExpression().accept(this);
        if (null != this.replacement) {
            whenClause.setThenExpression(this.replacement);
        }
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
    public void visit(SimilarColumn similarColumn) throws Exception {
    }

    @Override
    public void visit(Similarity similarity) throws Exception {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void visit(Relation relation) throws Exception {
        throw new UnsupportedOperationException(Error.getError("notImplemented"));
    }

    @Override
    public void visit(ArrayExpression array) throws Exception {
        throw new UnsupportedOperationException(Error.getError("notImplemented"));
    }

    @Override
    public void visit(RowExpression row) throws Exception {
        
    }

    @Override
    public void visit(FuzzyByExtension fuzzy) throws Exception {
        // Done
    }

    @Override
    public void visit(FuzzyTrapezoid fuzzy) throws Exception {     
        // Done
    }

    @Override
    public void visit(CastAsExpression cast) throws Exception {
        cast.getExpression().accept(this);
        if (null != this.replacement) {
            cast.setExpression(this.replacement);
        }
        this.replacement = null;
    }

}
