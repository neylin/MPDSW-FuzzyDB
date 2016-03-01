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
public abstract class ColumnOperation extends Operation {
    
    protected String schemaName;
    protected String tableName;
    protected String columnName;
    protected String domainId;
    protected String domainName;

    ColumnOperation(Connector connector) {
        super(connector);
    }

    protected String getSchemaTableForSQL() {
        return (schemaName != null ? schemaName + "." : "") + tableName;
    }

    protected String getDomainIdForSql() {
        return domainId != null ? domainId : "(SELECT domain_id FROM information_schema_fuzzy.domains WHERE table_schema = " + getSchemaNameForSql() + " AND domain_name = '" + domainName + "')";
    }

    protected String getSchemaNameForSql() {
        return schemaName != null ? "'" + schemaName + "'" : "(select current_schema())";
    }
    
    protected static String getSchemaTableForSQL(String schemaName, String tableName) {
        return (schemaName != null ? schemaName + "." : "") + tableName;
    }

    protected static String getDomainIdForSql(String schemaName, String domainName, Integer domainId) {
        return domainId != null ? "" + domainId : "(SELECT domain_id FROM information_schema_fuzzy.domains WHERE table_schema = " + ColumnOperation.getSchemaNameForSql(schemaName) + " AND domain_name = '" + domainName + "')";
    }

    protected static String getSchemaNameForSql(String schemaName) {
        return schemaName != null ? "'" + schemaName + "'" : "(select current_schema())";
    }
    
    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public String getDomainId() {
        return domainId;
    }

    public void setDomainId(String domainId) {
        this.domainId = domainId;
    }

    public String getSchemaName() {
        return schemaName;
    }

    public void setSchemaName(String schemaName) {
        this.schemaName = schemaName;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getDomainName() {
        return domainName;
    }

    public void setDomainName(String domainName) {
        this.domainName = domainName;
    }
    
}
