/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fuzzy.type2.operations;

import fuzzy.common.operations.Operation;
import fuzzy.common.translator.FuzzyColumn;
import fuzzy.common.translator.FuzzyColumnSet;
import fuzzy.database.Connector;
import fuzzy.helpers.Printer;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;
import net.sf.jsqlparser.expression.BinaryExpression;
import net.sf.jsqlparser.expression.CastAsExpression;
import net.sf.jsqlparser.expression.DoubleValue;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.NullValue;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.expression.fuzzy.FuzzyByExtension;
import net.sf.jsqlparser.expression.fuzzy.FuzzyTrapezoid;
import net.sf.jsqlparser.statement.table.ColDataType;

/**
 *
 * @author smaf
 */
public class ReplaceFuzzyType2ConstantOperation extends Operation {

    private Table table;
    private final List columns;
    private final List expressions;
    private String domain = null;
    private BinaryExpression binaryExpression;
    private Expression expression1;
    private Expression expression2;

    public ReplaceFuzzyType2ConstantOperation(Connector connector, Table table,
            List columns, List expressions) {
        super(connector);
        this.table = table;
        this.columns = columns;
        this.expressions = expressions;
        this.expression1 = null;
        this.expression2 = null;
    }

    public ReplaceFuzzyType2ConstantOperation(Connector connector, Table table,
            BinaryExpression binaryExpression) {
        super(connector);
        this.table = table;
        this.binaryExpression = binaryExpression;
        this.expression1 = binaryExpression.getLeftExpression();
        this.expression2 = binaryExpression.getRightExpression();
        this.expressions = null;
        this.columns = null;
    }

    /**
     * Replaces a given constant if it exists, otherwise exception is raised.
     *
     * @param schemaName schema where the query is executed.
     * @param tableName table where the constant wants to be replaced.
     * @param attributeName attribute tied to the constant.
     * @param expression expression that might be replaced
     * @return
     * @throws SQLException
     */
    public Expression replaceConstantIfExists(String schemaName, String tableName,
            String attributeName, Expression expression) throws SQLException {
        /* First check tha the column s of fuzzy type */
        String attributeIsFuzzy = "SELECT name, table_name, domain_name "
                + "FROM information_schema_fuzzy.columns2 as C, information_schema_fuzzy.domains2 as D "
                + "WHERE C.table_schema = '" + schemaName + "' "
                + "AND C.table_schema = D.table_schema "
                + "AND table_name = '" + tableName + "' "
                + "AND name = '" + attributeName + "' "
                + "AND domain_id = id;";
        Connector.ExecutionResult queryResult = this.connector.executeRaw(attributeIsFuzzy);
        if (queryResult.result.next()) {
            this.domain = queryResult.result.getString(3);
            /* If the expression is a string then its a type2 constant*/
            if ("string".equals(expression.getExpressionType())) {
                String getConstantValue = "SELECT name, table_name, constant_name, value, fuzzy_type, domain_name "
                        + "FROM information_schema_fuzzy.columns2, information_schema_fuzzy.constants2 "
                        + "WHERE table_schema = '" + schemaName + "' "
                        + "AND table_name='" + tableName + "' "
                        + "AND name = '" + attributeName + "' "
                        + "AND constant_schema = table_schema "
                        + "AND constant_name = " + expression.toString() + ";";
                queryResult = this.connector.executeRaw(getConstantValue);
                if (queryResult.result.next()) {
                    Expression returnExpression;
                    String value = queryResult.result.getString(4);
                    /* Parse the expression */
                    String possibilities = value.substring(value.indexOf("{") + 1, value.indexOf("}"));
                    value = value.substring(value.indexOf("}") + 1, value.length());
                    String values = value.substring(value.indexOf("{") + 1, value.indexOf("}"));
                    if ("fuzzyextension".equals(queryResult.result.getString(5))) {
                        /* Fuzzy by extension */
                        returnExpression = parseExtensionPossibilities(possibilities, values);
                    } else {
                        /* Fuzzy by trapezoid */
                        returnExpression = parseTrapezoidPossibilities(values);
                    }
                    return returnExpression;
                } else {
                    /* Raise exception if the constant does no exist */
                    throw new SQLException("Constant " + expression.toString() + " does not exist.", "42000", 3020, null);
                }
            } else if ("fuzzytrapezoid".equals(expression.getExpressionType())
                    || "fuzzyextension".equals(expression.getExpressionType())) {
                return expression;
            }
        }
        return null;
    }

    /**
     * Casts possibilities to Trapezoid Expression
     *
     * @param possibilitiesString string of possibilities
     * @return Expression
     */
    public static Expression parseTrapezoidPossibilities(String possibilitiesString) {
        String[] possibilitiesToParse = possibilitiesString.split(",");
        Expression[] trapezoidValues = new Expression[4];
        for (int i = 0; i < possibilitiesToParse.length; i++) {
            if ("NULL".equalsIgnoreCase(possibilitiesToParse[i])) {
                trapezoidValues[i] = new NullValue();
            } else {
                trapezoidValues[i] = new DoubleValue(possibilitiesToParse[i]);
            }
        }
        FuzzyTrapezoid f = new FuzzyTrapezoid(trapezoidValues[0], trapezoidValues[1],
                trapezoidValues[2], trapezoidValues[3]);
        return new FuzzyTrapezoid(trapezoidValues[0], trapezoidValues[1],
                trapezoidValues[2], trapezoidValues[3]);
    }

    /**
     * Casts possibilities to Extension Expression
     *
     * @param possibilitiesString string of possibilities
     * @param valuesString string of values
     * @return Expression
     */
    public static Expression parseExtensionPossibilities(String possibilitiesString, String valuesString) {
        String[] possibilitiesToParse = possibilitiesString.split(",");
        String[] valuesToParse = valuesString.split(",");
        FuzzyByExtension extension = null;
        for (int i = 0; i < valuesToParse.length; i++) {
            if (i == 0) {
                extension = new FuzzyByExtension(Double.parseDouble(possibilitiesToParse[i]), new DoubleValue(valuesToParse[i]));
            } else {
                extension.addPossibility(Double.parseDouble(possibilitiesToParse[i]), new DoubleValue(valuesToParse[i]));
            }
        }
        return extension;
    }

    public void iterateSelectedColumns(String schemaName) throws SQLException {
        Iterator iterator = this.columns.iterator();
        int counter = 0;
        while (iterator.hasNext()) {
            String attribute = iterator.next().toString();
            /* Replace the expression if needed. */
            Expression expression = replaceConstantIfExists(schemaName, this.table.getName(),
                    attribute, (Expression) this.expressions.get(counter));
            if (expression != null) {
                this.expressions.set(counter, expression);
            }
            counter++;
        }
    }

    public void iterateColumns(String schemaName) throws SQLException {
        String getColumns = "SELECT column_name "
                + "FROM information_schema.columns "
                + "WHERE table_schema = '" + schemaName + "' "
                + "AND table_name = '" + this.table.getName() + "'";
        Connector.ExecutionResult queryResult = this.connector.executeRaw(getColumns);
        String column;
        int counter = 0;
        while (queryResult.result.next()) {
            column = queryResult.result.getString(1);
            Expression expression = replaceConstantIfExists(schemaName, this.table.getName(),
                    column, (Expression) this.expressions.get(counter));
            if (expression != null) {
                this.expressions.set(counter, expression);
            }
            counter++;
        }
    }

    public boolean getTable(String columnName, String schemaName) throws SQLException {
        String getTable = "SELECT table_name "
                + "FROM information_schema_fuzzy.columns2 AS C, information_schema_fuzzy.domains2 AS D "
                + "WHERE name = '" + columnName + "' "
                + "AND C.table_schema = '" + schemaName + "' "
                + "AND C.table_schema = D.table_schema "
                + "AND domain_id = id";
        Connector.ExecutionResult query = this.connector.executeRaw(getTable);
        if (query.result.next()) {
            this.table = new Table(schemaName, query.result.getString(1));
            return true;
        }
        return false;
    }

    public Expression ifFuzzyColumnReplace(Expression column, Expression value, String schemaName) throws SQLException {
        if (getTable(column.toString(), schemaName)) {
            Expression expression = replaceConstantIfExists(schemaName, this.table.getName(),
                    column.toString(), value);
            if (expression != null) {
                ColDataType colDataType = new ColDataType();
                colDataType.setDataType(this.domain);
                CastAsExpression castAsExpression
                        = new CastAsExpression(expression, colDataType);
                return castAsExpression;
            }
        }
        return value;
    }

    @Override
    public void execute() throws SQLException {
        if (this.connector.getSchema().equals("")) {
            throw new SQLException("No database selected");
        }
        String schemaName = this.connector.getSchema();

        /* If the columns are listed */
        if (this.expression1 == null) {
            if (this.columns != null) {
                iterateSelectedColumns(schemaName);
            } else {
                iterateColumns(schemaName);
            }
        } else {
            if (!("column".equals(this.expression1.getExpressionType())
                    && "column".equals(this.expression2.getExpressionType()))) {
                if ("column".equals(this.expression1.getExpressionType())) {
                    this.expression2 = ifFuzzyColumnReplace(this.expression1, this.expression2, schemaName);
                    this.binaryExpression.setRightExpression(this.expression2);
                } else {
                    this.expression1 = ifFuzzyColumnReplace(this.expression2, this.expression1, schemaName);
                    this.binaryExpression.setLeftExpression(this.expression1);
                }
            }
        }
    }
}
