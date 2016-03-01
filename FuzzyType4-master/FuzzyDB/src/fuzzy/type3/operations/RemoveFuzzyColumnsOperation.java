/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fuzzy.type3.operations;

import fuzzy.common.operations.Operation;
import fuzzy.database.Connector;
import java.sql.SQLException;

/**
 *
 * @author bishma-stornelli
 */
public class RemoveFuzzyColumnsOperation extends Operation {

    protected String schemaName;
    protected String tableName;
    protected String columnName;

    public RemoveFuzzyColumnsOperation(Connector connector, String schemaName) {
        super(connector);
        this.schemaName = schemaName;
    }

    public RemoveFuzzyColumnsOperation(Connector connector, String schemaName, String tableName) {
        super(connector);
        this.schemaName = schemaName;
        this.tableName = tableName;
    }

    public RemoveFuzzyColumnsOperation(Connector connector, String schemaName, String tableName, String columnName) {
        super(connector);
        this.schemaName = schemaName;
        this.tableName = tableName;
        this.columnName = columnName;
    }    

    /**
     * Get the value of schemaName
     *
     * @return the value of schemaName
     */
    public String getSchemaName() {
        return schemaName;
    }

    /**
     * Set the value of schemaName
     *
     * @param schemaName new value of schemaName
     */
    public void setSchemaName(String schemaName) {
        this.schemaName = schemaName;
    }

    /**
     * Get the value of tableName
     *
     * @return the value of tableName
     */
    public String getTableName() {
        return tableName;
    }

    /**
     * Set the value of tableName
     *
     * @param tableName new value of tableName
     */
    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    /**
     * Get the value of columnName
     *
     * @return the value of columnName
     */
    public String getColumnName() {
        return columnName;
    }

    /**
     * Set the value of columnName
     *
     * @param columnName new value of columnName
     */
    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    @Override
    public void execute() throws SQLException {
        connector.executeRawUpdate(getQuery());
    }
    
    public String getQuery() {
        String query = "DELETE FROM information_schema_fuzzy.columns "
                + "WHERE table_schema = '" + schemaName + "'";
        if (tableName != null)
            query += " AND table_name = '" + tableName + "'";
        if (columnName != null)
            query += " AND column_name = '" + columnName + "'";
        return query;
    }
}
