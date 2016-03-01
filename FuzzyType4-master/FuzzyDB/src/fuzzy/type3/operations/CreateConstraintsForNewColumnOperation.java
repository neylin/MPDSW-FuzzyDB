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
public class CreateConstraintsForNewColumnOperation extends ColumnOperation {
    
    public CreateConstraintsForNewColumnOperation(Connector connector) {
        super(connector);
    }

    @Override
    public void execute() throws SQLException {
        // TODO what happens if these queries fail?
        String addForeignKeyConstraint = "ALTER TABLE "
            + getSchemaTableForSQL() + " "
            + "ADD CONSTRAINT FOREIGN KEY (" + columnName + ") "
            + "REFERENCES information_schema_fuzzy.labels (label_id) "
            + "ON UPDATE CASCADE ON DELETE RESTRICT";
        
        connector.executeRawUpdate(addForeignKeyConstraint);
    
        String addCheckConstraint = "ALTER TABLE " 
                + getSchemaTableForSQL() + " "
                + "ADD CONSTRAINT CHECK ("+ columnName + " IN ("
                + "SELECT label_id "
                + "FROM information_schema_fuzzy.labels "
                + "WHERE domain_id = " + getDomainIdForSql() + "))";
        
        connector.executeRawUpdate(addCheckConstraint);
    }
}
