/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fuzzy.type3.operations;

import fuzzy.common.operations.Operation;
import fuzzy.database.Connector;
import fuzzy.common.translator.Translator;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * @author bishma-stornelli
 */
public class CreateFuzzyDomainFromColumnOperation extends Operation {

    protected String domainName;
    protected String schemaName;
    protected String tableName;
    protected String columnName;

    public CreateFuzzyDomainFromColumnOperation(Connector connector, String domainName,
            String schemaName, String tableName, String columnName) {
        
        super(connector);
        
        this.domainName = domainName;
        this.schemaName = schemaName;
        this.tableName  = tableName;
        this.columnName = columnName;
    }

    @Override
    public void execute() throws SQLException {
        
        if ( this.connector.getSchema().equals("") ) {
            throw new SQLException(fuzzy.helpers.Error.getError("getSchemaT5"));
        }
        
        String countSql = "SELECT COUNT(*) FROM "
                + (schemaName != null ? schemaName + "." : "")
                + tableName + " WHERE " + columnName + " IS NOT NULL";
        
        ResultSet count = connector.executeRawQuery(countSql);
        
        if ( count.next() && count.getInt(1) == 0 ) {
            throw Translator.FR_EMPTY_VALUES_LIST(schemaName, tableName, columnName);
        }
        
        String insertDomain = "INSERT INTO information_schema_fuzzy.domains "
                + "VALUES (DEFAULT,'" + schemaName + "', '" + domainName + "', 3, NULL)";
        
        Integer domainId = null;
        
        try {
            domainId = connector.executeRawInsert(insertDomain);
        } catch (SQLException ex) {
            throw Translator.FR_DUPLICATE_DOMAIN_NAME(domainName);
        }
        
        String insertLabels = "INSERT INTO information_schema_fuzzy.labels " +
                "(domain_id, label_name) " +
                "SELECT DISTINCT " + domainId + ", " + columnName +
                " FROM " + (schemaName != null ? schemaName + "." : "") + tableName +
                " WHERE " + columnName + " IS NOT NULL";
        
        String insertSimilarities = "INSERT INTO information_schema_fuzzy.similarities "
                + "SELECT label_id, label_id, 1.0, TRUE "
                + "FROM information_schema_fuzzy.labels "
                + "WHERE domain_id = " + domainId;
        
        connector.executeRawUpdate(insertLabels);
        connector.executeRawUpdate(insertSimilarities);
    }

    public String getDomainName() {
        return domainName;
    }

    public String getSchemaName() {
        return schemaName;
    }

    public String getTableName() {
        return tableName;
    }

    public String getColumnName() {
        return columnName;
    }
}