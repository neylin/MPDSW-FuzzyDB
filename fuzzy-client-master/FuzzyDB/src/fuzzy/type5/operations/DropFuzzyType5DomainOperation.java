package fuzzy.type5.operations;

/**
 *
 * @author Jose Sanchez
 */

import fuzzy.common.operations.Operation;
import fuzzy.database.Connector;
import fuzzy.helpers.Logger;
import java.sql.SQLException;
import java.sql.Savepoint;

public class DropFuzzyType5DomainOperation extends Operation {

    private final String domain;

    public DropFuzzyType5DomainOperation(Connector connector, String domain) {
        super(connector);
        this.domain = domain;
    }

    @Override
    public void execute() throws SQLException {
        if (this.connector.getSchema().equals("")) {
            throw new SQLException("No database selected");
        }
        
        Logger.debug("Starting DROP FUZZY DOMAIN 5 " + domain + " operation");
        
        String schemaName = this.connector.getSchema();
        String fullTypeName = schemaName + "." + domain;
        
        String updateCatalog = "DELETE FROM information_schema_fuzzy.domains " +
            "WHERE table_schema = (select current_schema())" + 
            "AND domain_name = '" + domain + "' AND domain_type = 5";
        

        String funcNameFormat = schemaName + ".__" + domain + "_%s";
        
        
        String dropFuncFormat = "DROP FUNCTION IF EXISTS " + funcNameFormat + "(" + fullTypeName + ", TEXT)";
        String dropFuzzyFunc = String.format(dropFuncFormat, "f");
        
        String dropType = "DROP TYPE IF EXISTS "+domain+";";
        
        Savepoint sp = this.beginTransaction();
        try {
            connector.executeRawUpdate(updateCatalog);
            connector.executeRaw(dropFuzzyFunc);
            connector.executeRaw(dropType);
            
            this.commitTransaction();
        } catch (SQLException e) {
            this.rollback(sp);
            throw e;
        }
    }
    
}
