/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fuzzy.type3.operations;

import fuzzy.database.Connector;
import java.sql.SQLException;

/**
 *
 * @author bishma-stornelli
 */
public class InsertNewColumnsOperation extends ColumnOperation {

    public InsertNewColumnsOperation(Connector connector) {
        super(connector);
    }
    
    @Override
    public void execute() throws SQLException {
        // TODO what happens if this query fails?
        String insertIntoColumns = "INSERT INTO information_schema_fuzzy.columns "
            + "VALUES (" + getSchemaNameForSql() +", "
                + "'" + tableName + "', "
                + "'" + columnName + "', "
                + getDomainIdForSql() + ")";
        
        connector.executeRawUpdate(insertIntoColumns);
    }
    
}
