/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fuzzy.type3.operations;

import fuzzy.common.operations.Operation;
import fuzzy.database.Connector;
import fuzzy.helpers.Logger;
import fuzzy.common.translator.Translator;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * @author bishma-stornelli
 */
public class ChangeColumnOperation extends Operation {

    protected String schemaName;
    protected String tableName;
    protected String oldColumnName;
    protected String newColumnName;
    protected String dataType;
    protected String options;

    public ChangeColumnOperation(Connector connector) {
        super(connector);
    }

    @Override
    public void execute() throws SQLException {
        if (this.connector.getSchema().equals("")) {
            throw new SQLException("No database selected");
        }
        this.schemaName = this.connector.getSchema();

        /**
         * CHECK IF CURRENT DATA TYPE IS VARCHAR
         */        
        String currentTypeVarcharSQL = "SELECT data_type "
                + "FROM information_schema.columns "
                + "WHERE table_schema = " + ColumnOperation.getSchemaNameForSql(schemaName) + " "
                + "AND table_name = '" + tableName + "' "
                + "AND column_name = '" + oldColumnName + "'";

        ResultSet currentTypeRS = connector.executeRawQuery(currentTypeVarcharSQL);
        if (!currentTypeRS.next()) {
            // (a) no hay un esquema seleccionado, (b) no existe la tabla table_name, o (c) no existe la columna column_name
            // Next query will rise that error
            connector.executeRawUpdate("ALTER TABLE " + ColumnOperation.getSchemaTableForSQL(schemaName, tableName) + " "
                + "CHANGE " + oldColumnName + " " + newColumnName + " INTEGER " + (options != null ? options : ""));
        } else {
            String currentType = currentTypeRS.getString(1);
            if (!currentType.equalsIgnoreCase("varchar")) {
                // TODO throw unsupported type error
            }
        }
        ResultSet domainIdRS = connector.executeRawQuery(ColumnOperation.getDomainIdForSql(schemaName, dataType, null));
        if (!domainIdRS.next()) {
            // No existe el dominio
            throw Translator.FR_UNKNOWN_DOMAIN(dataType);
        }
        Integer domainId = domainIdRS.getInt(1);
        
        /**
         * CHECK IF ALL VALUES IN CURRENT COLUMN MATCH A LABEL IN THE DOMAIN
         */
        String subsetOfDomainSQL = "SELECT " + oldColumnName
                + " FROM " + ColumnOperation.getSchemaTableForSQL(schemaName, tableName) + " "
                + "LEFT JOIN information_schema_fuzzy.labels AS l "
                + "ON (" + tableName + "." + oldColumnName + " = l.label_name "
                + "AND l.domain_id = " 
                + domainId
                + ") "
                + "WHERE " + tableName + "." + oldColumnName + " IS NOT NULL "
                + "AND l.label_name IS NULL LIMIT 1 "
                + "FOR UPDATE";
        ResultSet subsetOfDomainRS = null;
        try {
            subsetOfDomainRS = connector.executeRawQuery(subsetOfDomainSQL);
        } catch (SQLException ex) {
            if (ex.getMessage().startsWith("Unknown column '" + tableName + "." + oldColumnName +"'")) {
                throw new SQLException("Unknown column '" + tableName + "." + oldColumnName +"' in '" + tableName + "'", ex.getSQLState(), ex.getErrorCode());
            }
            throw ex;
        }
        if (subsetOfDomainRS.next()) {
            // no es un subconjunto
            throw Translator.FR_LABEL_DO_NOT_EXISTS(subsetOfDomainRS.getString(oldColumnName));
        }
        
        /**
         * UPDATE CURRENT VALUES TO LABEL IDS
         */
        String updateValuesToLabelIdSQL = "UPDATE " + ColumnOperation.getSchemaTableForSQL(schemaName, tableName) + " "
                + "SET " + oldColumnName + " = ("
                + "SELECT label_id "
                + "FROM information_schema_fuzzy.labels "
                + "WHERE label_name = " + oldColumnName + " "
                + "AND domain_id = ("
                + "SELECT domain_id "
                + "FROM information_schema_fuzzy.domains "
                + "WHERE domain_name = '" + dataType + "' "
                + "AND table_schema = " + ColumnOperation.getSchemaNameForSql(schemaName) + "))";
        connector.executeRawUpdate(updateValuesToLabelIdSQL);        
        
        /**
         * CHANGE COLUMN TYPE TO INTEGER (which is safe since we already updated values to label ids)
         */
        String changeColumnType = "ALTER TABLE " + ColumnOperation.getSchemaTableForSQL(schemaName, tableName) + " "
                + "CHANGE " + oldColumnName + " " + newColumnName + " INTEGER " + (options != null ? options : "");
        connector.executeRawUpdate(changeColumnType);
        // If this fails, we need to revert the update

        /**
         * ADD FK AND CHECK CONSTRAINTS FROM NEW FUZZY COLUMN TO LABELS
         */
        CreateConstraintsForNewColumnOperation ccfnco = 
                new CreateConstraintsForNewColumnOperation(connector);
        ccfnco.setColumnName(newColumnName);
        ccfnco.setTableName(tableName);
        ccfnco.setSchemaName(schemaName);
        ccfnco.setDomainName(dataType);
        ccfnco.execute();

        /**
         * ADD NEW COLUMN TO COLUMNS (METADATA)
         */
        InsertNewColumnsOperation inco = 
                new InsertNewColumnsOperation(connector);
        inco.setColumnName(newColumnName);
        inco.setTableName(tableName);
        inco.setSchemaName(schemaName);
        inco.setDomainName(dataType);
        inco.execute();

    }

    /**
     * Get the value of options
     *
     * @return the value of options
     */
    public String getOptions() {
        return options;
    }

    /**
     * Set the value of options
     *
     * @param options new value of options
     */
    public void setOptions(String options) {
        this.options = options;
    }

    /**
     * Get the value of dataType
     *
     * @return the value of dataType
     */
    public String getDataType() {
        return dataType;
    }

    /**
     * Set the value of dataType
     *
     * @param dataType new value of dataType
     */
    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    /**
     * Get the value of newColumnName
     *
     * @return the value of newColumnName
     */
    public String getNewColumnName() {
        return newColumnName;
    }

    /**
     * Set the value of newColumnName
     *
     * @param newColumnName new value of newColumnName
     */
    public void setNewColumnName(String newColumnName) {
        this.newColumnName = newColumnName;
    }

    /**
     * Get the value of oldColumnName
     *
     * @return the value of oldColumnName
     */
    public String getOldColumnName() {
        return oldColumnName;
    }

    /**
     * Set the value of oldColumnName
     *
     * @param oldColumnName new value of oldColumnName
     */
    public void setOldColumnName(String oldColumnName) {
        this.oldColumnName = oldColumnName;
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

    public String getSchemaName() {
        return schemaName;
    }

    public void setSchemaName(String schemaName) {
        this.schemaName = schemaName;
    }
    
    
}
