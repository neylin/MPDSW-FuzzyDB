/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fuzzy.type3.translator;

import fuzzy.common.translator.FuzzyColumnSet;
import fuzzy.common.translator.FuzzyColumn;
import fuzzy.common.translator.AliasGenerator;
import fuzzy.helpers.Logger;
import java.util.ArrayList;
import java.util.List;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.Function;
import net.sf.jsqlparser.expression.SimilarColumn;
import net.sf.jsqlparser.expression.operators.relational.ExpressionList;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.select.SelectExpressionItem;

/**
 *
 * @author bishma-stornelli
 */
public class GroupByExpressionTranslator extends ExpressionColumnVisitor {
    
    FuzzyColumnSet fuzzyColumnSet;
    private final AliasGenerator aliasGenerator;
    protected List<Expression> aggregationFunctionParameters;

    GroupByExpressionTranslator(FuzzyColumnSet fuzzyColumnSet, AliasGenerator aliasGenerator, List<Expression> aggregationFunctionParameters) {
        this.fuzzyColumnSet = fuzzyColumnSet;
        this.aliasGenerator = aliasGenerator;
        this.aggregationFunctionParameters = aggregationFunctionParameters;
    }

    @Override
    public void visit(Column column) throws Exception {
        Logger.debug("Visiting column " + column.toString());
        // This will visit columns in GROUP BY only     
        FuzzyColumn fuzzyColumn = null;
        if ((fuzzyColumn = fuzzyColumnSet.get(column)) != null) {
            // Classic GROUP BY
            fuzzyColumn.includeFuzzyLabelLeftJoin(aliasGenerator);
            // Cambiar columna a L1.label_name
            column.setTable(fuzzyColumn.getFuzzyLabelColumn().getTable());
            column.setColumnName(fuzzyColumn.getFuzzyLabelColumn().getColumnName());
        }
    }
    
    @Override
    public void visit(SimilarColumn similarColumn) throws Exception {
        Logger.debug("Visiting similarColumn " + similarColumn.toString());
        // This will visit columns in GROUP BY only     
        FuzzyColumn fuzzyColumn = null;
        Column column = similarColumn.getColumn();
        if ((fuzzyColumn = fuzzyColumnSet.get(column)) != null) {
            fuzzyColumn.includeGroupByJoin(aliasGenerator);

            column.setTable(fuzzyColumn.getFuzzyLabelColumn().getTable());
            column.setColumnName(fuzzyColumn.getFuzzyLabelColumn().getColumnName());
            
            Logger.debug("Adding " + fuzzyColumn.getGroupBySimilarityColumn() + " to aggregationFunctionParameters");
            this.aggregationFunctionParameters.add(fuzzyColumn.getGroupBySimilarityColumn());
        } else {
            throw Translator.FR_NO_FUZZY_COLUMN;
        }
    }
    
    @Override
    public void visit(Function function) throws Exception {
        Logger.debug("Visiting function");
        if (!isFuzzyGroupBy()) {
            return;
        }
        if (!function.getName().equalsIgnoreCase("COUNT")) {
            // INVALID AGGREGATION FUNCTION FOR FUZZY GROUP BY
            throw Translator.FR_NON_SUPPORTED_FUNCTION;
        }        
        // I need to change the COUNT(*) to SUM(LEAST(...))
        Logger.debug("Changing COUNT to SUM");
        function.setName("SUM");        
        function.setAllColumns(false);
        function.setParameters(new ExpressionList(new ArrayList<Expression>()));
        if (aggregationFunctionParameters.size() == 1) {
            Logger.debug("Only one column, parameter of SUM: " + aggregationFunctionParameters.get(0).toString());
            function.getParameters().getExpressions().add(aggregationFunctionParameters.get(0));
        } else {
            Function leastFunction = new Function();
            leastFunction.setName("LEAST");
            leastFunction.setParameters(new ExpressionList(aggregationFunctionParameters));
            
            Logger.debug("Many columns, parameter of SUM: " + leastFunction.toString());
            function.getParameters().getExpressions().add(leastFunction);                    
        }
    }
    
    @Override
    public void visit(SelectExpressionItem sei) throws Exception {
        // It will enter here when it's translating SelectExpressionItem
        String alias = sei.getAlias() != null ? sei.getAlias() : "\"" + sei.toString() + "\"";
        sei.getExpression().accept(this);
        if ((sei.getExpression() instanceof Function) && isFuzzyGroupBy()) {
            // If it's a function and a fuzzy group by, and it enters to this if clause
            // the function is valid (and a COUNT) so the alias should be "COUNT(*)"
            // if it's not present
            sei.setAlias(alias);
        }
    }

    private boolean isFuzzyGroupBy() {
        return this.aggregationFunctionParameters.size() > 0;
    }
    
}
