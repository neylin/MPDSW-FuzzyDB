/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fuzzy.common.translator;

import fuzzy.database.Connector;
import fuzzy.helpers.Helper;
import fuzzy.helpers.Logger;
import fuzzy.helpers.Memory;
import fuzzy.type3.translator.ExpressionColumnVisitor;
import java.sql.SQLException;
import java.util.AbstractSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.StringValue;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.select.AllColumns;
import net.sf.jsqlparser.statement.select.AllTableColumns;
import net.sf.jsqlparser.statement.select.OrderByElement;
import net.sf.jsqlparser.statement.select.OrderByVisitor;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.SelectExpressionItem;
import net.sf.jsqlparser.statement.select.SelectItem;

/**
 * Create the column reference set using the SelectItems in the SELECT
 * clause, matching them with the tables found in the tableRefSet.
 */
public class FuzzyColumnSet implements Iterable<FuzzyColumn> {

    protected Connector connector;
    /**
     * Keys are stored as:
     *  alias if present
     *  schemaName.tableName.columnName if not
     */
    protected HashMap<String, FuzzyColumn> dict;
    protected Set<String> keys;
    protected TableRefList tableRefSet;
    protected int fuzzyType;


    public FuzzyColumnSet(Connector connector, TableRefList tableRefSet, PlainSelect plainSelect, int fuzzyType) throws Exception {
        this.fuzzyType = fuzzyType;
        this.connector = connector;
        this.tableRefSet = tableRefSet;
        dict = new HashMap();
        Logger.debug("Exploring SelectItems to look for fuzzy columns");
        ExpressionColumnRegister eCR = new ExpressionColumnRegister();
        for (SelectItem selectItem : (List<SelectItem>) plainSelect.getSelectItems()) {
            selectItem.accept(eCR);
        }
        Logger.debug("Exploring Where items to look for fuzzy columns");
        if (null != plainSelect.getWhere()) {
            plainSelect.getWhere().accept(eCR);
        }
        
        if (plainSelect.getGroupByColumnReferences() != null) {
            for (Expression e: (List<Expression>)plainSelect.getGroupByColumnReferences()) {
                e.accept(eCR);
            }
        }
        Logger.debug("Exploring OrderByElements to look for fuzzy columns");
        if (plainSelect.getOrderByElements() != null) {
            for (OrderByElement orderByElement : (List<OrderByElement>) plainSelect.getOrderByElements()) {
                orderByElement.accept(eCR);
            }
        }
        keys = dict.keySet();
    }

    public FuzzyColumn get(Column column) {
        String columnQualifiedName = FuzzyColumn.getQualifiedName(column);
        // columnQualifiedName has the format: [[schemaName.]tableName.]columnName
        for (FuzzyColumn fuzzyColumn : this) {
            // fuzzyColumnQualifiedName has the format: [tableAlias.]columnName | [[schemaName.]tableName.]columnName
            String fuzzyColumnQualifiedName = fuzzyColumn.getQualifiedName();
            if (columnQualifiedName.equals(fuzzyColumnQualifiedName)) {
                return fuzzyColumn;
            }
        }
        // But it may be the case that columnQualifiedName is tableName.columnName and
        // fuzzyColumnQualifiedName is schemaName.tableName.columnName so we need to
        // compare without the schemaName
        // Note that columnName may be ambigous but currently TableRefList is checking it's not
        for (FuzzyColumn fuzzyColumn : this) {
            String qualifiedName = fuzzyColumn.getQualifiedName(false);
            if (columnQualifiedName.equals(qualifiedName)) {
                return fuzzyColumn;
            }
        }
        // Lastly if columnQualifiedName is columnName, we must to compare againts
        // the bare name of the fuzzyColumn
        for (FuzzyColumn fuzzyColumn : this) {
            String qualifiedName = fuzzyColumn.getPublicName();
            if (columnQualifiedName.equals(qualifiedName)) {
                return fuzzyColumn;
            }
        }
        return null;
    }

    private class ExpressionColumnRegister extends ExpressionColumnVisitor implements OrderByVisitor {

        /**
         * Get the table ref from the FROM that creates this column in the SELECT
         * @param column column to be looked up among the FROM tables
         * @return the TableRef with the container table ref in the FROM
         */
        @Override
        public void visit(Column column) throws SQLException {
            if (column.getColumnName().startsWith("'") || column.getColumnName().startsWith("\"")) {
                // The parser represents a literal ("Barcelona", 'Caracas') as a column but it's not
                return;
            }
            Logger.debug("Visiting column " + column.toString());
            Table table = column.getTable();
            String name = column.getColumnName();
            TableRef foundTable = null;
            // qualified column
            if (null != table.getName()) { // Column is "[schema_name.]table_name.column_name"
                Logger.debug("This column has the table name prepended");
                foundTable = tableRefSet.get(table);
                // qualification doesn't refer to a valid table in the query
                if (null == foundTable) {
                    throw new SQLException("Unknown column '" + table.getName() + "."
                            + name + "' in 'field list'", "42S22", 1054);
                } else {
                    // only register this column if the table is found
                    registerFuzzyColumn(foundTable, column);
                }
                return;
            }
            // no qualified column, check all tables to see where we can find it
            Logger.debug("Looking for the table in the whole list");
            for (TableRef tableRef : tableRefSet.getList()) {
                // do not check SubSelect. Those columns are recursively translated
                if (tableRef.getTableType() != TableRef.TableType.TABLE) {
                    continue;
                }
                // check all columns of this table to see if there's a match
                String schemaName = Helper.getSchemaName(connector, tableRef.getTable());
                String tableName = tableRef.getTable().getName();
                for (String columnName : Memory.getColumns(connector, schemaName, tableName)) {
                    if (!columnName.equals(name)) {
                        continue;
                    }
                    // more than one table contain this unqualified column
                    if (null != foundTable) {
                        // Para ver por qué comenté esto, leer mi rage en el siguiente null == foundTable
                        //throw new SQLException("Column '" + name + "' in field list is ambiguous",
                        //        "23000", 1052);
                    }
                    // this is the table container. the lastone will prevail
                    foundTable = tableRef;
                }
            }
            // no table in query has this column
            if (null == foundTable) {
                // Borré esto porque estaba jodiendo la vida.
                // Si renombro una columna utilizando AS, e intento ordenar por esa
                // columna, esta vaina me reventaba, lo cual no tiene sentido.
                // Sinceramente, que se jodan los desarrolladores anteriores y
                // su código roto.
                // Además el idiota JDBC tampoco me deja hacer algo como getString("a.column")
                // O sea, no puedo acceder a las columnas usando su nombre calificado.
                // A la mierda con Java.
                //throw new SQLException("Unknown column '" + name + "' in 'field list'",
                //        "42S22", 1054);
            } else {
                // only register this column if the table is found
                registerFuzzyColumn(foundTable, column);
            }
        }

        @Override
        public void visit(AllColumns ac) throws Exception {
            // add all columns from all tables
            for (TableRef tableRef : tableRefSet.getList()) {
                if (tableRef.getTableType() != TableRef.TableType.TABLE) {
                    continue;
                }
                Table table = tableRef.getTable();
                // this already checks the table exists
                AbstractSet<String> cols = Memory.getColumns(connector, Helper.getSchemaName(connector, table), table.getName());
                for (String col : cols) {
                    registerFuzzyColumn(tableRef, new Column(table, col));
                }
            }
        }

        @Override
        public void visit(AllTableColumns atc) throws Exception {
            Logger.debug("Exploring AllTableColumns to find fuzzy columns");

            TableRef tableRef = tableRefSet.get(atc.getTable());
            // this already checks existent table
            AbstractSet<String> cols = Memory.getColumns(connector, tableRef.getTable());
            for (String col : cols) {
                registerFuzzyColumn(tableRef, new Column(tableRef.getTable(), col));
            }
        }

        @Override
        public void visit(SelectExpressionItem sei) throws Exception {
            sei.getExpression().accept(this);
        }

        @Override
        public void visit(OrderByElement obe) throws Exception {
            Expression expression = obe.getExpression();
            Expression fuzzyStart = obe.getFuzzyStart();
            Logger.debug("FuzzyColumnSet <196>: Just before accept this as visitor for OrderByElement");
            expression.accept(this);
            //TODO Check what to do with fuzzyStart
            if (fuzzyStart != null) {
                if (!(fuzzyStart instanceof StringValue)) {
                    throw Translator.FR_INVALID_STARTING_VALUE;
                } else {
                    // TODO if starting value is not a label in the domain I should throw an error
                }
            }
        }
    }

    @Override
    public Iterator<FuzzyColumn> iterator() {
        return new CRIterator<FuzzyColumn>();
    }

    private class CRIterator<FuzzyColumn> implements Iterator {

        private Iterator<String> keysIterator = null;

        CRIterator() {
            keysIterator = keys.iterator();
        }

        @Override
        public boolean hasNext() {
            return keysIterator.hasNext();
        }

        @Override
        public FuzzyColumn next() {
            return (FuzzyColumn) dict.get(keysIterator.next());
        }

        @Override
        public void remove() {
        }
    }

    /**
     * Add this 
     * @param tableRef
     * @param column 
     */
    private void registerFuzzyColumn(TableRef tableRef, Column column)
            throws SQLException {
        // it is safe to assume we found a fuzzy column in a Table. SubSelects are ignored.
        String schemaName = Helper.getSchemaName(connector, tableRef.getTable());
        String tableName = tableRef.getTable().getName();
        if (this.fuzzyType == 3 && !Memory.isFuzzyColumn(connector, schemaName, tableName, column.getColumnName())) {
            return;
        }
        if (this.fuzzyType == 2 && !Memory.isFuzzyType2Column(connector, schemaName, tableName, column.getColumnName())) {
            return;
        }
        if (this.fuzzyType == 5 && !Memory.isFuzzyType5Column(connector, schemaName, tableName, column.getColumnName())) {
            return;
        }
        String qualifiedName = FuzzyColumn.getQualifiedName(column);
        if (!dict.containsKey(qualifiedName)) {
            Logger.debug("Registering " + qualifiedName + " as fuzzy column");
            dict.put(qualifiedName, new FuzzyColumn(tableRef, column.getColumnName()));
        }
    }

    /**
     * Outputs to stdout a human readable information about the internal data
     * structures content.
     */
    public void debugDump() throws SQLException {

        System.out.println("Is empty ColumnRefSet: " + dict.isEmpty());
        for (FuzzyColumn columnRef : this) {
            String val = columnRef.getTableRef().getId();
            System.out.println(columnRef.getQualifiedName() + " => " + val);
        }
    }
}
