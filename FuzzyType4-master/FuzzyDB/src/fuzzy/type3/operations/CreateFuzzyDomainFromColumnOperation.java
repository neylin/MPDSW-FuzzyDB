/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fuzzy.type3.operations;

import fuzzy.common.operations.Operation;
import fuzzy.database.Connector;
import fuzzy.type3.ddl.Relation;
import fuzzy.helpers.Logger;
import fuzzy.type3.translator.Translator;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author bishma-stornelli
 */
public class CreateFuzzyDomainFromColumnOperation extends Operation {

    protected String domainName;
    protected String schemaName;
    protected String tableName;
    protected String columnName;

    public CreateFuzzyDomainFromColumnOperation(Connector connector) {
        super(connector);
    }

    @Override
    public void execute() throws SQLException {
        if (this.connector.getSchema().equals("")) {
            throw new SQLException("No database selected");
        }
        this.schemaName = this.connector.getSchema();

        String countSql = "SELECT COUNT(*) FROM "
                + (schemaName != null ? schemaName + "." : "")
                + tableName + " WHERE " + columnName + " IS NOT NULL";
        Logger.debug("Validating values with: " + countSql);
        ResultSet count = connector.executeRawQuery(countSql);
        if (count.next() && count.getInt(1) == 0) {
            throw Translator.FR_EMPTY_VALUES_LIST(schemaName, tableName, columnName);
        }
        // TODO this should be refactored to some better place
        String insertDomain = "INSERT INTO information_schema_fuzzy.domains "
                + "VALUES (DEFAULT, (select current_schema()), '" + domainName + "')";
        Logger.debug("Inserting domain with: " + insertDomain);
        Integer domainId = null;
        try {
            domainId = connector.executeRawInsert(insertDomain);
        } catch (SQLException ex) {
            throw Translator.FR_DUPLICATE_DOMAIN_NAME(domainName);
            // FIXME: La migración a Postgres arruinó este catch. Por ahora solo lanzo un 'dominio duplicado'
            // FIXME: (aunque no chequeo si esa fue la razón real para el error)

            /*String message = ex.getMessage();
            if (ex.getErrorCode() == 1062) { // duplicated key
                if (message.contains("Duplicate entry") && message.contains("for key 'table_schema'")) {
                    throw Translator.FR_DUPLICATE_DOMAIN_NAME(domainName);
                }            
            } else if (ex.getErrorCode() == 1048) {// not database selected
                if (message.contains("Column 'table_schema' cannot be null")) {
                    throw Translator.ER_NO_DB_ERROR;
                }
            }
            throw ex;*/
        }
        
        String insertLabels = "INSERT INTO information_schema_fuzzy.labels " +
                "(domain_id, label_name) " +
                "SELECT DISTINCT " + domainId + ", " + columnName +
                " FROM " + (schemaName != null ? schemaName + "." : "") + tableName +
                " WHERE " + columnName + " IS NOT NULL";
        Logger.debug("Inserting labels with: " + insertLabels);
        connector.executeRawUpdate(insertLabels);
        
        String insertSimilarities = "INSERT INTO information_schema_fuzzy.similarities "
                + "SELECT label_id, label_id, 1.0, TRUE "
                + "FROM information_schema_fuzzy.labels "
                + "WHERE domain_id = " + domainId;
        Logger.debug("Inserting similarities with: " + insertSimilarities);
        connector.executeRawUpdate(insertSimilarities);
    }

    public String getDomainName() {
        return domainName;
    }

    public void setDomainName(String domainName) {
        this.domainName = domainName;
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
}
