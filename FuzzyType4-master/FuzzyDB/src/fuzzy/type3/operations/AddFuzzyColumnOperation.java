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
public class AddFuzzyColumnOperation extends Operation {
    
    protected String schemaName;
    protected String tableName;
    protected String columnName;
    protected Integer domainId;

    public AddFuzzyColumnOperation(Connector connector,
                                   String schemaName, 
                                   String tableName, 
                                   String columnName, 
                                   Integer domainId) {
        super(connector);
        this.schemaName = schemaName;
        this.tableName = tableName;
        this.columnName = columnName;
        this.domainId = domainId;
    }
    
    @Override
    public void execute() throws SQLException {
        connector.executeRawUpdate(getQuery());
    }

    public String getQuery() {
        return "INSERT INTO information_schema_fuzzy.columns "
                            + "VALUES ('"
                            + schemaName +"','"
                            + tableName + "','"
                            + columnName + "',"
                            + domainId
                            + ")";
    }

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public Integer getDomainId() {
        return domainId;
    }

    public void setDomainId(Integer domainId) {
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
    
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final AddFuzzyColumnOperation other = (AddFuzzyColumnOperation) obj;
        if ((this.schemaName == null) ? (other.schemaName != null) : !this.schemaName.equals(other.schemaName)) {
            return false;
        }
        if ((this.tableName == null) ? (other.tableName != null) : !this.tableName.equals(other.tableName)) {
            return false;
        }
        if ((this.columnName == null) ? (other.columnName != null) : !this.columnName.equals(other.columnName)) {
            return false;
        }
        if (this.domainId != other.domainId && (this.domainId == null || !this.domainId.equals(other.domainId))) {
            return false;
        }
        return true;
    }
    
    
}
